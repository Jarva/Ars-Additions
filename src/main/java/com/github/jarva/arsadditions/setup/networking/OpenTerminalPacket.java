package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.curios.WarpIndex;
import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Function;

public record OpenTerminalPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenTerminalPacket> TYPE = new CustomPacketPayload.Type<>(ArsAdditions.prefix("open_terminal"));
    public static final StreamCodec<ByteBuf, OpenTerminalPacket> STREAM_CODEC = StreamCodec.unit(new OpenTerminalPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            ItemStack t = PlayerInvUtil.findItem(sender, i -> i.getItem() instanceof WarpIndex, ItemStack.EMPTY, Function.identity());
            if(!t.isEmpty() && t.getItem() instanceof WarpIndex remote) {
                remote.open(sender, t);
            }
        });
    }

    public static void openTerminal() {
        NetworkHandler.sendToServer(new OpenTerminalPacket());
    }
}
