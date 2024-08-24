package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record CharmData(int charges) {
    public static Codec<CharmData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("charges", 0).forGetter(CharmData::charges)
    ).apply(instance, CharmData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, CharmData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CharmData::charges, CharmData::new
    );

    public CharmData use(int charges) {
        return set(this.charges - charges);
    }

    public CharmData set(int charges) {
        return new CharmData(Math.max(charges, 0));
    }

    public CharmData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.CHARM_DATA, this);
    }

    public static CharmData getOrDefault(ItemStack stack) {
        return getOrDefault(stack, 0);
    }

    public static CharmData getOrDefault(ItemStack stack, int charges) {
        return stack.getOrDefault(AddonDataComponentRegistry.CHARM_DATA, new CharmData(charges));
    }
}
