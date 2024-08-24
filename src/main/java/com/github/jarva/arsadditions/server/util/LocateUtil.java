package com.github.jarva.arsadditions.server.util;

import brightspark.asynclocator.AsyncLocator;
import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.ExplorationScrollData;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

public class LocateUtil {
    public enum Status {
        PENDING,
        SUCCESS,
        FAILURE,
    }

    public record LocateState(Status status, Structure structure, BlockPos pos) {
        public static LocateState pending() {
            return new LocateState(Status.PENDING, null, null);
        }
        public static LocateState failure() {
            return new LocateState(Status.FAILURE, null, null);
        }
        public static LocateState success(Structure structure, BlockPos pos) {
            return new LocateState(Status.SUCCESS, structure, pos);
        }
    }

    private static final Cache<UUID, LocateState> STRUCTURE_LOOKUP_CACHE =
            CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(Duration.ofMinutes(5)).build();

    public static HolderSet<Structure> holderFromTag(ServerLevel level, TagKey<Structure> structureTagKey) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.getTag(structureTagKey).orElseThrow();
    }

    public static HolderSet<Structure> holderFromResource(ServerLevel level, ResourceKey<Structure> structureResourceKey) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.getHolder(structureResourceKey).map(HolderSet::direct).orElseThrow();
    }

    public static void resolveUUID(ServerLevel level, Vec3 position, ItemStack stack, @Nullable Entity entity) {
        if (!stack.has(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA)) return;
        UUID uuid = stack.get(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA);
        LocateState state = STRUCTURE_LOOKUP_CACHE.getIfPresent(uuid);
        if (state == null) {
            ArsAdditions.LOGGER.warn("Found Exploration Warp Scroll with no pending structure lookup.");
            locateFromStack(level, position, stack);
        }
        if (state == null || state.status() == Status.PENDING) return;
        stack.remove(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA);
        if (state.status() == Status.FAILURE) {
            PortUtil.sendMessageNoSpam(entity, Component.translatable("tooltip.ars_additions.exploration_warp_scroll.failed"));
            STRUCTURE_LOOKUP_CACHE.invalidate(uuid);
        } else {
            setScrollData(level, stack, state.pos());
            PortUtil.sendMessageNoSpam(entity, Component.translatable("tooltip.ars_additions.exploration_warp_scroll.located"));
        }
    }

    public static boolean isPending(ItemStack stack) {
        return stack.has(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA);
    }

    public static void locateWithState(ItemStack itemStack, ServerLevel level, HolderSet<Structure> holderSet, BlockPos origin, int searchRadius, boolean skipKnownStructures) {
        UUID uuid = UUID.randomUUID();
        if (itemStack.has(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA)) {
            uuid = itemStack.get(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA);
        } else {
            itemStack.set(AddonDataComponentRegistry.STRUCTURE_LOOKUP_DATA, uuid);
        }

        STRUCTURE_LOOKUP_CACHE.put(uuid, LocateState.pending());
        UUID finalUuid = uuid;
        locate(level, holderSet, origin, searchRadius, skipKnownStructures, (pair) -> {
            if (pair == null) {
                STRUCTURE_LOOKUP_CACHE.put(finalUuid, LocateState.failure());
                return;
            }
            BlockPos pos = pair.getFirst();
            if (pos == null) {
                STRUCTURE_LOOKUP_CACHE.put(finalUuid, LocateState.failure());
            } else {
                STRUCTURE_LOOKUP_CACHE.put(finalUuid, LocateState.success(pair.getSecond().value(), pos));
            }
        });
    }

    public static void locateFromStack(ServerLevel level, Vec3 position, ItemStack stack) {
        ExplorationScrollData data = ExplorationScrollData.fromItemStack(stack);
        HolderSet<Structure> holderSet = LocateUtil.holderFromTag(level, ExplorationScrollData.DEFAULT_DESTINATION);
        Vec3 origin = data.pos().orElse(position);
        int searchRadius = ExplorationScrollData.DEFAULT_SEARCH_RADIUS;
        boolean skipKnown = ExplorationScrollData.DEFAULT_SKIP_EXISTING;
        LocateUtil.locateWithState(stack, level, holderSet, BlockPos.containing(origin), searchRadius, skipKnown);
    }

    public static void locate(ServerLevel level, HolderSet<Structure> holderSet, BlockPos origin, int searchRadius, boolean skipKnownStructures, Consumer<Pair<BlockPos, Holder<Structure>>> consumer) {
        if (ModList.get().isLoaded("asynclocator")) {
            AsyncLocator.locate(level, holderSet, origin, searchRadius, skipKnownStructures).then((pair) -> {
                if (pair == null) {
                    level.getServer().submit(() -> consumer.accept(null));
                    return;
                }
                Pair<BlockPos, Holder<Structure>> modified = pair.mapFirst(pos -> findBlockPos(level, pair.getSecond().value(), pos));
                level.getServer().submit(() -> consumer.accept(modified));
            });
        } else {
            ArsAdditions.LOGGER.warn("Running locate on server thread. If this causes lag please install Async Locator https://modrinth.com/mod/async-locator");
            Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level, holderSet, origin, searchRadius, skipKnownStructures);
            if (pair == null) {
                consumer.accept(null);
                return;
            }
            Pair<BlockPos, Holder<Structure>> modified = pair.mapFirst(pos -> findBlockPos(level, pair.getSecond().value(), pos));
            consumer.accept(modified);
        }
    }

    public static WarpScrollData setScrollData(ServerLevel level, ItemStack stack, BlockPos pos) {
        return stack.update(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true), (data) ->
            data.setPos(pos, level.dimension().location().toString()).setRotation(Vec2.ZERO)
        );
    }

    public static BlockPos findBlockPos(ServerLevel level, Structure structure, BlockPos pos) {
        StructureStart structureStart = level.structureManager().getStartForStructure(SectionPos.of(pos), structure, level.getChunk(pos));
        if (structureStart == null) {
            BlockPos highest = findHighestSafeBlock(level, pos);
            if (highest == null) return pos.atY(level.getSeaLevel());
            return highest;
        };

        BoundingBox box = structureStart.getBoundingBox();

        for (int i = 0; i < 5; i++) {
            int x = Mth.randomBetweenInclusive(level.random, box.minX(), box.maxX());
            int y = Mth.randomBetweenInclusive(level.random, box.minY(), box.maxY());
            int z = Mth.randomBetweenInclusive(level.random, box.minZ(), box.maxZ());
            BlockPos start = new BlockPos(x, y, z);

            for (BlockPos.MutableBlockPos position : BlockPos.spiralAround(start, 10, Direction.NORTH, Direction.EAST)) {
                BlockPos highest = findHighestSafeBlock(level, position, y, box.minY());
                if (highest != null) return highest;
            }
        }

        return findHighestSafeBlock(level, pos);
    }

    public static BlockPos findHighestSafeBlock(ServerLevel level, BlockPos pos) {
        return findHighestSafeBlock(level, pos, level.getMaxBuildHeight(), level.getMinBuildHeight());
    }

    public static BlockPos findHighestSafeBlock(ServerLevel level, BlockPos pos, int max, int min) {
        BlockPos.MutableBlockPos mutable = pos.mutable().setY(max);
        boolean headAir = level.getBlockState(mutable).isAir();
        mutable.move(Direction.DOWN);

        boolean groundAir;
        for(boolean feetAir = level.getBlockState(mutable).isAir(); mutable.getY() > min; feetAir = groundAir) {
            mutable.move(Direction.DOWN);
            groundAir = level.getBlockState(mutable).isAir();
            if (!groundAir && feetAir && headAir) {
                return mutable.move(Direction.UP);
            }

            headAir = feetAir;
        }

        return null;
    }
}
