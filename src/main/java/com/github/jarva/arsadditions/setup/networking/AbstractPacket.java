package com.github.jarva.arsadditions.setup.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface AbstractPacket {

    default void toBytes(FriendlyByteBuf buf) {}
    default void handle(Supplier<NetworkEvent.Context> ctx) {};
}
