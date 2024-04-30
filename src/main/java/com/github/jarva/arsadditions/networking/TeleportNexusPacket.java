package com.github.jarva.arsadditions.networking;

import com.github.jarva.arsadditions.block.WarpNexus;
import com.github.jarva.arsadditions.capability.CapabilityRegistry;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.util.TeleportUtil;
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
            BlockState bs = player.level().getBlockState(pos);
            if (!bs.is(AddonBlockRegistry.WARP_NEXUS.get())) return;
            if (player.blockPosition().distToCenterSqr(pos.getX(), pos.getY(), pos.getZ()) > Math.pow(player.getBlockReach(), 2)) return;
            ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY).orElse(new ItemStackHandler());
            ItemStack scroll = nexus.getStackInSlot(index);
            WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(scroll);
            if (player instanceof ServerPlayer serverPlayer) {
                if (bs.getValue(WarpNexus.REQUIRES_SOURCE)) {
                    ISpecialSourceProvider takePos = SourceUtil.takeSource(pos, serverPlayer.serverLevel(), 5, 1000);
                    if (takePos != null) {
                        TeleportUtil.teleport(serverPlayer.serverLevel(), data, serverPlayer);
                    } else {
                        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.apparatus.nomana"));
                    }
                } else {
                    TeleportUtil.teleport(serverPlayer.serverLevel(), data, serverPlayer);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void teleport(int index, BlockPos pos) {
        NetworkHandler.sendToServer(new TeleportNexusPacket(index, pos));
    }
}
