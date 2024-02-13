package com.github.jarva.arsadditions.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.item.WarpIndex;
import com.github.jarva.arsadditions.util.PlayerInvUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ArsAdditions.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static void init() {
        INSTANCE.registerMessage(1, OpenTerminalPacket.class, (a, b) -> {}, b -> new OpenTerminalPacket(), NetworkHandler::handleData);
    }

    public static void handleData(OpenTerminalPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            ItemStack t = PlayerInvUtil.findItem(sender, i -> i.getItem() instanceof WarpIndex, ItemStack.EMPTY, Function.identity());
            if(!t.isEmpty() && t.getItem() instanceof WarpIndex remote) {
                remote.open(sender, t);
            }
        });
    }

    public static void openTerminal() {
        INSTANCE.sendToServer(new OpenTerminalPacket());
    }
}