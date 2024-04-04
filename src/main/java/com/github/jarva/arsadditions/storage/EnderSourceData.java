package com.github.jarva.arsadditions.storage;

import com.github.jarva.arsadditions.sync.SourceJarSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnderSourceData extends SavedData {
    public HashMap<UUID, Integer> source = new HashMap<>();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        for (Map.Entry<UUID, Integer> entry : source.entrySet()) {
            UUID uuid = entry.getKey();
            Integer source = entry.getValue();

            tag.putInt(uuid.toString(), source);
        }
        return tag;
    }

    public void update(UUID uuid, int source) {
        this.source.put(uuid, source);
        setDirty();
    }

    public static EnderSourceData create() {
        return new EnderSourceData();
    }

    public static EnderSourceData load(CompoundTag tag) {
        EnderSourceData data = EnderSourceData.create();
        List<UUID> uuids = tag.getAllKeys().stream().map(UUID::fromString).toList();
        for (UUID uuid : uuids) {
            data.source.put(uuid, tag.getInt(uuid.toString()));
        }
        return data;
    }

    public static EnderSourceData getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(EnderSourceData::load, EnderSourceData::create, "ender_source_data");
    }

    public static int getSource(MinecraftServer server, UUID uuid) {
        return EnderSourceData.getData(server).source.computeIfAbsent(uuid, k -> 0);
    }

    public static void setSource(MinecraftServer server, UUID uuid, int source) {
        if (source != EnderSourceData.getSource(server, uuid)) {
            EnderSourceData data = EnderSourceData.getData(server);
            data.update(uuid, source);
            SourceJarSync.updateSourceLevel(server, uuid);
        }
    }
}
