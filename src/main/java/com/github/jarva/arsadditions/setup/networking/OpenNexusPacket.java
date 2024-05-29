package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.client.util.ClientUtil;
import com.github.jarva.arsadditions.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenNexusPacket implements AbstractPacket {
    private final BlockPos pos;
    private final CompoundTag tag;
    public OpenNexusPacket(FriendlyByteBuf buf) {
        tag = buf.readNbt();
        pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeBlockPos(pos);
    }

    public OpenNexusPacket(CompoundTag famCaps, BlockPos nexus) {
        this.tag = famCaps;
        this.pos = nexus;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ArsNouveau.proxy.getPlayer();
            ItemStackHandler itemStackHandler = new ItemStackHandler(9);
            itemStackHandler.deserializeNBT(tag);
            ClientUtil.openWarpScreen(ContainerLevelAccess.create(player.level(), pos), itemStackHandler);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void openNexus(Player player, BlockPos pos) {
        ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY).orElse(new ItemStackHandler());
        CompoundTag tag = nexus.serializeNBT();
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayerClient(new OpenNexusPacket(tag, pos), serverPlayer);
        }
    }
}
