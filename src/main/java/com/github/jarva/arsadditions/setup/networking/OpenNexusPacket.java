package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.client.util.ClientUtil;
import com.github.jarva.arsadditions.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenNexusPacket(BlockPos pos, CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenNexusPacket> TYPE = new CustomPacketPayload.Type<>(ArsAdditions.prefix("open_nexus"));

    public static final StreamCodec<ByteBuf, OpenNexusPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenNexusPacket::pos,
            ByteBufCodecs.COMPOUND_TAG, OpenNexusPacket::tag,
            OpenNexusPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = ArsNouveau.proxy.getPlayer();
            ItemStackHandler itemStackHandler = new ItemStackHandler(9);
            itemStackHandler.deserializeNBT(player.registryAccess(), tag);
            ClientUtil.openWarpScreen(ContainerLevelAccess.create(player.level(), pos), itemStackHandler);
        });
    }

    public static void openNexus(Player player, BlockPos pos) {
        ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS);
        if (nexus == null) nexus = new ItemStackHandler(9);
        CompoundTag tag = nexus.serializeNBT(player.registryAccess());
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayerClient(new OpenNexusPacket(pos, tag), serverPlayer);
        }
    }
}
