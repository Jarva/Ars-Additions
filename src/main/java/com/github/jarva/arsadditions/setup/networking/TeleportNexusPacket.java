package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.capability.CapabilityRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportNexusPacket implements AbstractPacket {
    private final BlockPos pos;
    int index;
    public TeleportNexusPacket(FriendlyByteBuf buf) {
        index = buf.readInt();
        pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index);
        buf.writeBlockPos(pos);
    }

    public TeleportNexusPacket(int index, BlockPos pos) {
        this.index = index;
        this.pos = pos;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player == null) return;
            BlockState bs = player.getLevel().getBlockState(pos);
            if (!bs.is(AddonBlockRegistry.WARP_NEXUS.get())) return;
            if (player.blockPosition().distToCenterSqr(pos.getX(), pos.getY(), pos.getZ()) > Math.pow(player.getReachDistance(), 2)) return;
            ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY).orElse(new ItemStackHandler());
            ItemStack scroll = nexus.getStackInSlot(index);
            WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(scroll);
            if (player instanceof ServerPlayer serverPlayer) {
                if (bs.getValue(WarpNexus.REQUIRES_SOURCE)) {
                    ISpecialSourceProvider takePos = SourceUtil.takeSource(pos, serverPlayer.getLevel(), 5, 1000);
                    if (takePos != null) {
                        TeleportUtil.teleport(serverPlayer.getLevel(), data, serverPlayer);
                    } else {
                        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.apparatus.nomana"));
                    }
                } else {
                    TeleportUtil.teleport(serverPlayer.getLevel(), data, serverPlayer);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void teleport(int index, BlockPos pos) {
        NetworkHandler.sendToServer(new TeleportNexusPacket(index, pos));
    }
}
