package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static int nextID() {
        return ID++;
    }

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ArsAdditions.prefix( "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(nextID(), OpenTerminalPacket.class, OpenTerminalPacket::toBytes, OpenTerminalPacket::new, OpenTerminalPacket::handleData);
        INSTANCE.registerMessage(nextID(), OpenNexusPacket.class, OpenNexusPacket::toBytes, OpenNexusPacket::new, OpenNexusPacket::handle);
        INSTANCE.registerMessage(nextID(), TeleportNexusPacket.class, TeleportNexusPacket::toBytes, TeleportNexusPacket::new, TeleportNexusPacket::handle);
    }

    public static void sendToPlayerClient(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }
}