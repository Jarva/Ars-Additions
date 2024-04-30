package com.github.jarva.arsadditions.networking;

import com.github.jarva.arsadditions.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncNexusPacket implements AbstractPacket {
    CompoundTag tag;
    public SyncNexusPacket(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public SyncNexusPacket(CompoundTag famCaps) {
        this.tag = famCaps;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ArsNouveau.proxy.getPlayer();
            ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY).orElse(new ItemStackHandler());
            if (nexus != null) {
                nexus.deserializeNBT(tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void syncCapability(Player player) {
        ItemStackHandler nexus = player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY).orElse(new ItemStackHandler());
        CompoundTag tag = nexus.serializeNBT();
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayerClient(new SyncNexusPacket(tag), serverPlayer);
        }
    }
}
