package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.MemoryCrystal;
import com.github.jarva.arsadditions.common.item.data.MemoryCrystalData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.github.jarva.arsadditions.common.item.MemoryCrystal.CLEAR_SLOT_INDEX;
import static com.github.jarva.arsadditions.common.item.MemoryCrystal.LOCK_SLOT_INDEX;

public record PacketMemoryCrystalAction(int slot) implements CustomPacketPayload {

    public static final Type<PacketMemoryCrystalAction> TYPE =
        new Type<>(ArsAdditions.prefix("memory_crystal_action"));

    public static final StreamCodec<ByteBuf, PacketMemoryCrystalAction> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PacketMemoryCrystalAction::slot,
            PacketMemoryCrystalAction::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = MemoryCrystal.getCrystalFromHand(player);

            if (!MemoryCrystal.isHoldingCrystal(player)) {
                return;
            }

            if (slot == CLEAR_SLOT_INDEX) {
                handleClear(player, stack);
            } else if (slot == LOCK_SLOT_INDEX) {
                handleLockToggle(player, stack);
            } else if (slot >= 0 && slot < MemoryCrystalData.MAX_SLOTS) {
                handleSlotSelection(player, stack, slot);
            }
        });
    }

    private void handleSlotSelection(Player player, ItemStack stack, int slotIndex) {
        MemoryCrystalData.fromItemStack(stack)
            .withSelectedSlot(slotIndex)
            .write(stack);

        PortUtil.sendMessage(player,
            Component.translatable("tooltip.ars_additions.memory_crystal.selected_slot", slotIndex + 1)
                .withStyle(ChatFormatting.GREEN));
    }

    private void handleClear(Player player, ItemStack stack) {
        MemoryCrystalData crystalData = MemoryCrystalData.fromItemStack(stack);

        if (crystalData.isSelectedLocked()) {
            PortUtil.sendMessage(player,
                Component.translatable("chat.ars_additions.memory_crystal.slot_locked")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        if (!crystalData.hasData()) {
            PortUtil.sendMessage(player,
                Component.translatable("chat.ars_additions.memory_crystal.already_empty", crystalData.selectedSlot() + 1)
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }

        crystalData.clearSelectedSlot().write(stack);
        PortUtil.sendMessage(player,
            Component.translatable("chat.ars_additions.memory_crystal.cleared", crystalData.selectedSlot() + 1)
                .withStyle(ChatFormatting.GREEN));
    }

    private void handleLockToggle(Player player, ItemStack stack) {
        MemoryCrystalData crystalData = MemoryCrystalData.fromItemStack(stack);
        boolean wasLocked = crystalData.isSelectedLocked();

        if (!wasLocked && !crystalData.hasData()) {
            PortUtil.sendMessage(player,
                Component.translatable("chat.ars_additions.memory_crystal.cannot_lock_empty", crystalData.selectedSlot() + 1)
                    .withStyle(ChatFormatting.RED));
            return;
        }

        crystalData.toggleSelectedLock().write(stack);

        String messageKey = wasLocked
            ? "chat.ars_additions.memory_crystal.unlocked"
            : "chat.ars_additions.memory_crystal.locked";

        PortUtil.sendMessage(player,
            Component.translatable(messageKey, crystalData.selectedSlot() + 1)
                .withStyle(ChatFormatting.GREEN));
    }
}
