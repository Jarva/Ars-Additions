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
import java.util.function.BiConsumer;

public class SourceJarSync {
    public static Map<ResourceKey<Level>, Set<BlockPos>> posMap = new ConcurrentHashMap<>();
    public static List<BiConsumer<UUID, Integer>> contraptions = new LinkedList<>();

    public static void addPosition(Level world, BlockPos pos) {
        ResourceKey<Level> key = world.dimension();
        posMap.computeIfAbsent(key, k -> new HashSet<>()).add(pos);
    }

    public static void updateSourceLevel(MinecraftServer server, UUID uuid) {
        int source = EnderSourceData.getSource(server, uuid);
        for (BiConsumer<UUID, Integer> consumer : contraptions)
            consumer.accept(uuid, source);
        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : posMap.entrySet()) {
            Level world = server.getLevel(entry.getKey());
            if (world == null) continue;

            Set<BlockPos> positions = entry.getValue();
            List<BlockPos> stale = new ArrayList<>();
            for (BlockPos p : positions) {
                if (!world.isLoaded(p)) continue;

                BlockEntity entity = world.getBlockEntity(p);
                if (entity instanceof EnderSourceJarTile tile) {
                    if (tile.getOwner() == null || !tile.getOwner().equals(uuid)) continue;
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
