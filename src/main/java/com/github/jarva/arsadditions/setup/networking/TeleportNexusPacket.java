package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.block.tile.WarpNexusTile;
import com.github.jarva.arsadditions.common.capability.CapabilityRegistry;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeleportNexusPacket(BlockPos pos, int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportNexusPacket> TYPE = new CustomPacketPayload.Type<>(ArsAdditions.prefix("teleport_nexus"));

    public static final StreamCodec<ByteBuf, TeleportNexusPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TeleportNexusPacket::pos,
            ByteBufCodecs.VAR_INT, TeleportNexusPacket::index,
            TeleportNexusPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            WarpNexusTile be = WarpNexusTile.getWarpNexus(player.level(), pos).orElse(null);
            if (be == null) return;

            if (player.blockPosition().distToCenterSqr(pos.getX(), pos.getY(), pos.getZ()) > Math.pow(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 2)) return;

            ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS);
            if (nexus == null) return;

            ItemStack scroll = nexus.getStackInSlot(index);
            WarpScrollData data = scroll.getOrDefault(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true));

            if (be.getBlockState().getValue(WarpNexus.REQUIRES_SOURCE)) {
                ISpecialSourceProvider takePos = SourceUtil.takeSource(pos, serverPlayer.serverLevel(), 5, 1000);
                if (takePos != null) {
                    TeleportUtil.teleport(serverPlayer.serverLevel(), data, player);
                } else {
                    PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.apparatus.nomana"));
                }
            } else {
                TeleportUtil.teleport(serverPlayer.serverLevel(), data, player);
            }
        });
    }

    public static void teleport(int index, BlockPos pos) {
        NetworkHandler.sendToServer(new TeleportNexusPacket(pos, index));
    }
}
