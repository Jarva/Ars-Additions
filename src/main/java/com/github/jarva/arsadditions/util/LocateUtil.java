package com.github.jarva.arsadditions.util;

import brightspark.asynclocator.AsyncLocator;
import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.loot.functions.ExplorationScrollFunction;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

public class LocateUtil {
    public enum Status {
        PENDING,
        SUCCESS,
        FAILURE,
    }

    public record LocateState(Status status, BlockPos pos) {
        public static LocateState pending() {
            return new LocateState(Status.PENDING, null);
        }
        public static LocateState failure() {
            return new LocateState(Status.FAILURE, null);
        }
        public static LocateState success(BlockPos pos) {
            return new LocateState(Status.SUCCESS, pos);
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

    public static final String STRUCTURE_LOOKUP_KEY = "async-lookup.uuid";

    public static void resolveUUID(ServerLevel level, Vec3 position, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(STRUCTURE_LOOKUP_KEY)) return;
        UUID uuid = tag.getUUID(STRUCTURE_LOOKUP_KEY);
        LocateState state = STRUCTURE_LOOKUP_CACHE.getIfPresent(uuid);
        if (state == null) {
            ArsAdditions.LOGGER.warn("Found Exploration Warp Scroll with no pending structure lookup.");
            locateFromStack(level, position, stack);
        }
        if (state == null || state.status() == Status.PENDING) return;
        stack.removeTagKey(STRUCTURE_LOOKUP_KEY);
        if (state.status() == Status.FAILURE) {
            STRUCTURE_LOOKUP_CACHE.invalidate(uuid);
        } else {
            BlockPos pos = state.pos();
            setScrollData(level, stack, pos);
        }
    }

    public static boolean isPending(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(STRUCTURE_LOOKUP_KEY);
    }

    public static void locateWithState(ItemStack itemStack, ServerLevel level, HolderSet<Structure> holderSet, BlockPos origin, int searchRadius, boolean skipKnownStructures) {
        UUID uuid = UUID.randomUUID();
        if (itemStack.hasTag() && itemStack.getTag().contains(STRUCTURE_LOOKUP_KEY)) {
            uuid = itemStack.getTag().getUUID(STRUCTURE_LOOKUP_KEY);
        } else {
            itemStack.getOrCreateTag().putUUID(STRUCTURE_LOOKUP_KEY, uuid);
        }

        STRUCTURE_LOOKUP_CACHE.put(uuid, LocateState.pending());
        UUID finalUuid = uuid;
        locate(level, holderSet, origin, searchRadius, skipKnownStructures, (pair) -> {
            BlockPos pos = pair.getFirst();
            if (pos == null) {
                STRUCTURE_LOOKUP_CACHE.put(finalUuid, LocateState.failure());
            } else {
                STRUCTURE_LOOKUP_CACHE.put(finalUuid, LocateState.success(pos));
            }
        });
    }

    public static void locateFromStack(ServerLevel level, Vec3 position, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        HolderSet<Structure> holderSet = LocateUtil.holderFromTag(level, ExplorationScrollFunction.DEFAULT_DESTINATION);
        Vec3 origin = position;
        int searchRadius = ExplorationScrollFunction.DEFAULT_SEARCH_RADIUS;
        boolean skipKnown = ExplorationScrollFunction.DEFAULT_SKIP_EXISTING;
        if (tag != null) {
            if (tag.contains("resource")) {
                ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(tag.getString("resource")));
                holderSet = LocateUtil.holderFromResource(level, key);
            }
            if (tag.contains("tag")) {
                TagKey<Structure> key = TagKey.create(Registries.STRUCTURE, new ResourceLocation(tag.getString("tag")));
                holderSet = LocateUtil.holderFromTag(level, key);
            }
            if (tag.contains("origin")) {
                CompoundTag originTag = tag.getCompound("origin");
                double x = originTag.getDouble("x");
                double y = originTag.getDouble("y");
                double z = originTag.getDouble("z");
                origin = new Vec3(x, y, z);
            }
            if (tag.contains("search_radius")) {
                searchRadius = tag.getInt("search_radius");
            }
            if (tag.contains("skip_known")) {
                skipKnown = tag.getBoolean("skip_known");
            }
        }
        LocateUtil.locateWithState(stack, level, holderSet, BlockPos.containing(origin), searchRadius, skipKnown);
    }

    private static void locate(ServerLevel level, HolderSet<Structure> holderSet, BlockPos origin, int searchRadius, boolean skipKnownStructures, Consumer<Pair<BlockPos, Holder<Structure>>> consumer) {
        if (ModList.get().isLoaded("asynclocator")) {
            AsyncLocator.locate(level, holderSet, origin, searchRadius, skipKnownStructures).thenOnServerThread(consumer);
        } else {
            ArsAdditions.LOGGER.warn("Running locate on server thread. If this causes lag please install Async Locator https://modrinth.com/mod/async-locator");
            Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level, holderSet, origin, searchRadius, skipKnownStructures);
            consumer.accept(pair);
        }
    }

    public static WarpScroll.WarpScrollData setScrollData(ServerLevel level, ItemStack stack, BlockPos pos) {
        WarpScroll.WarpScrollData data = new StableWarpScroll.StableScrollData(stack);
        BlockPos finalPos = pos;
        for (BlockPos.MutableBlockPos position : BlockPos.spiralAround(pos, 5, Direction.NORTH, Direction.EAST)) {
            BlockPos posHeightmap = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, position);
            boolean isSolidFloor = level.getBlockState(posHeightmap.below()).isSolid();
            boolean isAirFeet = level.getBlockState(posHeightmap).isAir();
            boolean isAirHead = level.getBlockState(posHeightmap.above()).isAir();
            if (isSolidFloor && isAirFeet && isAirHead) {
                finalPos = posHeightmap;
                break;
            }
        }

        if (finalPos == pos) {
            finalPos = finalPos.atY(level.getSeaLevel());
        }

        data.setData(finalPos, level.dimension().location().toString(), Vec2.ZERO);
        return data;
    }
}
