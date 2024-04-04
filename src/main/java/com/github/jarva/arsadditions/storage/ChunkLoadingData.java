package com.github.jarva.arsadditions.storage;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLoadingData extends SavedData {
    public Map<UUID, Set<ChunkPos>> chunks = new ConcurrentHashMap<>();

    public Set<ChunkPos> get(UUID uuid) {
        return chunks.getOrDefault(uuid, new HashSet<>());
    }

    public boolean updateChunk(UUID uuid, ChunkPos pos, boolean status) {
        Set<ChunkPos> chunkSet = chunks.getOrDefault(uuid, new HashSet<>());
        boolean updated = status ? chunkSet.add(pos) : chunkSet.remove(pos);
        if (updated) setDirty();
        return updated;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        for (Map.Entry<UUID, Set<ChunkPos>> entry : chunks.entrySet()) {
            ListTag chunkList = new ListTag();
            List<LongTag> tags = entry.getValue()
                    .stream()
                    .map(ChunkPos::toLong)
                    .map(LongTag::valueOf)
                    .toList();
            chunkList.addAll(tags);
            tag.put(entry.getKey().toString(), chunkList);
        }
        return tag;
    }

    public static ChunkLoadingData create() {
        return new ChunkLoadingData();
    }

    public static ChunkLoadingData load(CompoundTag tag) {
        ChunkLoadingData data = ChunkLoadingData.create();
        List<UUID> uuids = tag.getAllKeys().stream().map(UUID::fromString).toList();
        for (UUID uuid : uuids) {
            Set<ChunkPos> chunks = new HashSet<>();
            ListTag longs = tag.getList(uuid.toString(), Tag.TAG_LONG);
            for (Tag longChunk : longs) {
                if (longChunk instanceof LongTag longTag) {
                    chunks.add(new ChunkPos(longTag.getAsLong()));
                }
            }
            data.chunks.put(uuid, chunks);
        }
        return data;
    }

    public static ChunkLoadingData getData(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ChunkLoadingData::load, ChunkLoadingData::create, "chunkloading_data");
    }

    public static Map<ResourceKey<Level>, Set<ChunkPos>> getChunks(MinecraftServer server, UUID uuid) {
        HashMap<ResourceKey<Level>, Set<ChunkPos>> chunkMap = new HashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            Set<ChunkPos> chunks = getData(level).get(uuid);
            chunkMap.put(level.dimension(), chunks);
        }
        return chunkMap;
    }

    public static int countChunks(MinecraftServer server, UUID uuid) {
        return getChunks(server, uuid).values().stream().map(Set::size).reduce(0, Integer::sum);
    }

    public static boolean updateChunk(ServerLevel level, UUID uuid, ChunkPos pos, boolean shouldLoad) {
        boolean updated = getData(level).updateChunk(uuid, pos, shouldLoad);
        boolean isLoaded = level.getForcedChunks().contains(pos.toLong());
        if (isLoaded != shouldLoad) {
            level.getChunkSource().updateChunkForced(pos, shouldLoad);
        }
        return updated;
    }

    public static void loadChunks(MinecraftServer server, UUID uuid, boolean shouldLoad) {
        for (Map.Entry<ResourceKey<Level>, Set<ChunkPos>> entry : ChunkLoadingData.getChunks(server, uuid).entrySet()) {
            ServerLevel dim = server.getLevel(entry.getKey());
            if (dim == null) continue;
            LongSet forcedChunks = dim.getForcedChunks();
            ChunkSource source = dim.getChunkSource();
            for (ChunkPos chunk : entry.getValue()) {
                boolean isLoaded = forcedChunks.contains(chunk.toLong());
                if (!isLoaded) {
                    source.updateChunkForced(chunk, shouldLoad);
                }
            }
        }
    }
}
