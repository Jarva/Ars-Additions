package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MagicCarpetInventoryData(List<ItemStack> items) {
    public static final int SLOT_COUNT = 27;

    public static final Codec<MagicCarpetInventoryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(MagicCarpetInventoryData::items)
    ).apply(instance, MagicCarpetInventoryData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MagicCarpetInventoryData> STREAM_CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC,
            MagicCarpetInventoryData::items,
            MagicCarpetInventoryData::new
    );

    public MagicCarpetInventoryData {
        items = normalize(items);
    }

    private static List<ItemStack> normalize(List<ItemStack> source) {
        ArrayList<ItemStack> normalized = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            normalized.add(ItemStack.EMPTY);
        }
        for (int i = 0; i < Math.min(SLOT_COUNT, source.size()); i++) {
            ItemStack stack = source.get(i);
            normalized.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return List.copyOf(normalized);
    }

    public static MagicCarpetInventoryData empty() {
        return new MagicCarpetInventoryData(List.of());
    }

    public static Optional<MagicCarpetInventoryData> fromStack(ItemStack stack) {
        return Optional.ofNullable(stack.get(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY));
    }

    public List<ItemStack> mutableItems() {
        ArrayList<ItemStack> mutable = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = items.get(i);
            mutable.add(stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return mutable;
    }

    public MagicCarpetInventoryData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY, this);
    }
}
