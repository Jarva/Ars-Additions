package com.github.jarva.arsadditions.storage;

import com.github.jarva.arsadditions.source.SourceJarEventQueue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.UUID;

public class EnderSourceData extends SavedData {
    public HashMap<UUID, Integer> source = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public static EnderSourceData create() {
        return new EnderSourceData();
    }

    public static EnderSourceData load(CompoundTag tag) {
        return EnderSourceData.create();
    }

    public static EnderSourceData getData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(EnderSourceData::load, EnderSourceData::create, "ender_source_data");
    }

    public static int getSource(MinecraftServer server, UUID uuid) {
        return EnderSourceData.getData(server).source.computeIfAbsent(uuid, k -> 0);
    }

    public static void setSource(MinecraftServer server, UUID uuid, int source) {
        if (source != EnderSourceData.getSource(server, uuid)) {
            EnderSourceData.getData(server).source.put(uuid, source);
            SourceJarEventQueue.updateSourceLevel(server, uuid);
        }
    }
}
