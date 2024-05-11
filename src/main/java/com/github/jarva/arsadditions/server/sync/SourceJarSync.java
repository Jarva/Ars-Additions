package com.github.jarva.arsadditions.server.sync;

import com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile;
import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SourceJarSync {
    public static Map<ResourceKey<Level>, Set<BlockPos>> posMap = new ConcurrentHashMap<>();

    public static void addPosition(Level world, BlockPos pos) {
        ResourceKey<Level> key = world.dimension();
        posMap.computeIfAbsent(key, k -> new HashSet<>()).add(pos);
    }

    public static void updateSourceLevel(MinecraftServer server, UUID uuid) {
        int source = EnderSourceData.getSource(server, uuid);
        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : posMap.entrySet()) {
            Level world = server.getLevel(entry.getKey());
            if (world == null) continue;

            Set<BlockPos> positions = entry.getValue();
            List<BlockPos> stale = new ArrayList<>();
            for (BlockPos p : positions) {
                if (!world.isLoaded(p)) continue;

                BlockEntity entity = world.getExistingBlockEntity(p);
                if (entity instanceof EnderSourceJarTile tile) {
                    if (!tile.getOwner().equals(uuid)) continue;
                    if (tile.getSource() == source) continue;

                    tile.setSource(source);
                } else {
                    stale.add(p);
                }
            }
            for (BlockPos pos : stale) {
                positions.remove(pos);
            }
        }
    }
}
