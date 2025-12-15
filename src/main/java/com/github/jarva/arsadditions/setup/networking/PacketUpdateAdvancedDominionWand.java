package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.AdvancedDominionWand;
import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketUpdateAdvancedDominionWand(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketUpdateAdvancedDominionWand> TYPE =
            new CustomPacketPayload.Type<>(ArsAdditions.prefix("set_advanced_wand_mode"));

    public static final StreamCodec<ByteBuf, PacketUpdateAdvancedDominionWand> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    PacketUpdateAdvancedDominionWand::slot,
                    PacketUpdateAdvancedDominionWand::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            // Validate slot is within valid bounds
            if (slot < 0 || slot >= AdvancedDominionWand.AdvancedDominionSlots.values().length) {
                return;
            }

            ItemStack stack = player.getMainHandItem().getItem() instanceof AdvancedDominionWand ?
                    player.getMainHandItem() : player.getOffhandItem();

            // Verify player actually has the wand
            if (!(stack.getItem() instanceof AdvancedDominionWand)) {
                return;
            }

            AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);

            AdvancedDominionWand.AdvancedDominionSlots slotType = AdvancedDominionWand.AdvancedDominionSlots.values()[slot];
            switch (slotType) {
                case TOGGLE_ORDER -> stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), data.toggleLinkOrder());
                case CLEAR -> stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), data.clear());
                case TOGGLE_COUNT -> stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), data.toggleLinkCount());
            }
        });
    }
}
