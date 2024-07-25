package com.github.jarva.arsadditions.common.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record WayfinderData(Component text) {
    public static Codec<WayfinderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("text").forGetter(WayfinderData::text)
    ).apply(instance, WayfinderData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, WayfinderData> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, WayfinderData::text, WayfinderData::new
    );
}
