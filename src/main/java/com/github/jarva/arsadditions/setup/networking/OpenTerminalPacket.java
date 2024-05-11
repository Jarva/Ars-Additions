package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.common.item.WarpIndex;
import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public class OpenTerminalPacket implements AbstractPacket {
    public OpenTerminalPacket() {}
    public OpenTerminalPacket(FriendlyByteBuf buf) {}

    public void handleData(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            ItemStack t = PlayerInvUtil.findItem(sender, i -> i.getItem() instanceof WarpIndex, ItemStack.EMPTY, Function.identity());
            if(!t.isEmpty() && t.getItem() instanceof WarpIndex remote) {
                remote.open(sender, t);
            }
        });
    }

    public static void openTerminal() {
        NetworkHandler.INSTANCE.sendToServer(new OpenTerminalPacket());
    }
}
