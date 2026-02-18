package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MemoryCrystalData(
    List<MemorySlot> slots,
    int selectedSlot
) {
    public static final int MAX_SLOTS = 10;

    public MemoryCrystalData {
        slots = normalizeSlots(slots);
        selectedSlot = clampSelectedSlot(selectedSlot);
    }

    public record MemorySlot(Optional<CompoundTag> data, boolean locked) {
        public static final Codec<MemorySlot> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                CompoundTag.CODEC.optionalFieldOf("data")
                    .forGetter(MemorySlot::data),
                Codec.BOOL.optionalFieldOf("locked", false)
                    .forGetter(MemorySlot::locked)
            ).apply(instance, MemorySlot::new)
        );

        public static final MemorySlot EMPTY = new MemorySlot(Optional.empty(), false);
    }

    public static final Codec<MemoryCrystalData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            MemorySlot.CODEC.listOf()
                .optionalFieldOf("slots", createEmptySlots())
                .forGetter(MemoryCrystalData::slots),
            Codec.INT.optionalFieldOf("selectedSlot", 0)
                .forGetter(MemoryCrystalData::selectedSlot)
        ).apply(instance, MemoryCrystalData::new)
    );

    public static final StreamCodec<ByteBuf, MemoryCrystalData> STREAM_CODEC =
        ByteBufCodecs.fromCodec(CODEC);

    private static List<MemorySlot> createEmptySlots() {
        List<MemorySlot> slots = new ArrayList<>();
        for (int i = 0; i < MAX_SLOTS; i++) {
            slots.add(MemorySlot.EMPTY);
        }
        return slots;
    }

    private static List<MemorySlot> normalizeSlots(List<MemorySlot> slots) {
        List<MemorySlot> normalized = new ArrayList<>(MAX_SLOTS);
        if (slots != null) {
            int upperBound = Math.min(slots.size(), MAX_SLOTS);
            for (int i = 0; i < upperBound; i++) {
                MemorySlot slot = slots.get(i);
                normalized.add(slot == null ? MemorySlot.EMPTY : slot);
            }
        }
        while (normalized.size() < MAX_SLOTS) {
            normalized.add(MemorySlot.EMPTY);
        }
        return List.copyOf(normalized);
    }

    private static int clampSelectedSlot(int selectedSlot) {
        if (selectedSlot < 0) {
            return 0;
        }
        return Math.min(selectedSlot, MAX_SLOTS - 1);
    }

    public static MemoryCrystalData fromItemStack(ItemStack stack) {
        return stack.getOrDefault(
            AddonDataComponentRegistry.MEMORY_CRYSTAL_DATA.get(),
            new MemoryCrystalData(createEmptySlots(), 0)
        );
    }

    public MemoryCrystalData withData(CompoundTag data) {
        List<MemorySlot> newSlots = new ArrayList<>(this.slots);
        MemorySlot current = newSlots.get(selectedSlot);
        newSlots.set(selectedSlot, new MemorySlot(Optional.of(data), current.locked()));
        return new MemoryCrystalData(newSlots, selectedSlot);
    }

    public MemoryCrystalData withSelectedSlot(int slot) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            return this;
        }
        return new MemoryCrystalData(this.slots, slot);
    }

    public MemoryCrystalData clearSelectedSlot() {
        List<MemorySlot> newSlots = new ArrayList<>(this.slots);
        MemorySlot current = newSlots.get(selectedSlot);
        newSlots.set(selectedSlot, new MemorySlot(Optional.empty(), current.locked()));
        return new MemoryCrystalData(newSlots, selectedSlot);
    }

    public MemoryCrystalData toggleSelectedLock() {
        List<MemorySlot> newSlots = new ArrayList<>(this.slots);
        MemorySlot current = newSlots.get(selectedSlot);
        newSlots.set(selectedSlot, new MemorySlot(current.data(), !current.locked()));
        return new MemoryCrystalData(newSlots, selectedSlot);
    }

    public Optional<CompoundTag> getSelectedData() {
        return slots.get(selectedSlot).data();
    }

    public boolean hasData() {
        return slots.get(selectedSlot).data().isPresent();
    }

    public boolean isSelectedLocked() {
        return slots.get(selectedSlot).locked();
    }

    public boolean isSlotLocked(int slot) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            return false;
        }
        return slots.get(slot).locked();
    }

    public MemoryCrystalData write(ItemStack stack) {
        stack.set(AddonDataComponentRegistry.MEMORY_CRYSTAL_DATA.get(), this);
        return this;
    }
}
