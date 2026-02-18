package com.github.jarva.arsadditions.server.storage;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLoadingData extends SavedData {
    private static final String VERSION_KEY = "version";
    private static final int CURRENT_VERSION = 1;

    public static final TicketController TICKET_CONTROLLER = new TicketController(
            ArsAdditions.prefix("chunk_loading"),
            ChunkLoadingData::validateTickets
    );

    public Map<UUID, Set<ChunkPos>> chunks = new ConcurrentHashMap<>();
    private int version = CURRENT_VERSION;

    public Set<ChunkPos> get(UUID uuid) {
        return chunks.getOrDefault(uuid, new HashSet<>());
    }

    public boolean updateChunk(UUID uuid, ChunkPos pos, boolean status) {
        Set<ChunkPos> chunkSet = status ? chunks.computeIfAbsent(uuid, k -> new HashSet<>()) : chunks.get(uuid);
        if (chunkSet == null) {
            return false;
        }

        boolean updated = status ? chunkSet.add(pos) : chunkSet.remove(pos);
        if (updated) {
            if (!status && chunkSet.isEmpty()) {
                chunks.remove(uuid);
            }
            setDirty();
        }
        return updated;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putInt(VERSION_KEY, version);
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

    public static ChunkLoadingData load(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ChunkLoadingData data = ChunkLoadingData.create();
        data.version = tag.getInt(VERSION_KEY);
        List<UUID> uuids = tag.getAllKeys().stream().filter(key -> !key.equals(VERSION_KEY)).map(UUID::fromString).toList();
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
        return level.getDataStorage().computeIfAbsent(new Factory<>(ChunkLoadingData::create, ChunkLoadingData::load), "chunkloading_data");
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

    private static void logInfo(boolean shouldLoad, ChunkPos pos, UUID uuid) {
        if (!ServerConfig.SERVER.chunkloading_log_loading.get()) return;
        if (shouldLoad) {
            ArsAdditions.LOGGER.info("[Chunk Loader] Forceloading {} for {}", pos, uuid);
        } else {
            ArsAdditions.LOGGER.info("[Chunk Loader] Unloading {} for {}", pos, uuid);
        }
    }

    public static boolean updateChunk(ServerLevel level, UUID uuid, ChunkPos pos, boolean shouldLoad) {
        boolean updated = getData(level).updateChunk(uuid, pos, shouldLoad);
        if (updated) {
            logInfo(shouldLoad, pos, uuid);
            TICKET_CONTROLLER.forceChunk(level, uuid, pos.x, pos.z, shouldLoad, true);
        }
        return updated;
    }

    public static void loadChunks(MinecraftServer server, UUID uuid, boolean shouldLoad) {
        for (Map.Entry<ResourceKey<Level>, Set<ChunkPos>> entry : ChunkLoadingData.getChunks(server, uuid).entrySet()) {
            ServerLevel dim = server.getLevel(entry.getKey());
            if (dim == null) continue;
            for (ChunkPos chunk : entry.getValue()) {
                logInfo(shouldLoad, chunk, uuid);
                TICKET_CONTROLLER.forceChunk(dim, uuid, chunk.x, chunk.z, shouldLoad, true);
            }
        }
    }

    private static void validateTickets(ServerLevel level, TicketHelper ticketHelper) {
        ChunkLoadingData data = getData(level);

        // Migrate from vanilla forced chunks (version 0 -> 1)
        if (data.version == 0) {
            ArsAdditions.LOGGER.info("[Chunk Loader] Migrating vanilla forced chunks for dimension {}", level.dimension().location());
            for (Map.Entry<UUID, Set<ChunkPos>> entry : data.chunks.entrySet()) {
                UUID uuid = entry.getKey();
                for (ChunkPos pos : entry.getValue()) {
                    if (level.getForcedChunks().contains(pos.toLong())) {
                        level.setChunkForced(pos.x, pos.z, false);
                    }
                    TICKET_CONTROLLER.forceChunk(level, uuid, pos.x, pos.z, true, true);
                }
            }
            data.version = CURRENT_VERSION;
            data.setDirty();
        }

        // Prune stale tickets: remove tickets for UUIDs not in our saved data
        for (UUID uuid : new HashSet<>(ticketHelper.getEntityTickets().keySet())) {
            if (!data.chunks.containsKey(uuid)) {
                ticketHelper.removeAllTickets(uuid);
            }
        }
    }
}
