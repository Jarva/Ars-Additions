package com.github.jarva.arsadditions.util;

import brightspark.asynclocator.AsyncLocator;
import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fml.ModList;

import java.util.function.Consumer;

public class LocateUtil {
    public static HolderSet<Structure> getFromTag(ServerLevel level, TagKey<Structure> structureTagKey) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.getTag(structureTagKey).orElseThrow();
    }

    public static HolderSet<Structure> getFromResource(ServerLevel level, ResourceKey<Structure> structureResourceKey) {
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return registry.getHolder(structureResourceKey).map(HolderSet::direct).orElseThrow();
    }

    public static void locate(ServerLevel level, HolderSet<Structure> holderSet, BlockPos origin, int searchRadius, boolean skipKnownStructures, Consumer<Pair<BlockPos, Holder<Structure>>> consumer) {
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
