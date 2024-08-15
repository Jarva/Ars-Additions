package com.github.jarva.arsadditions.common.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WarpBindData(GlobalPos pos) {
    public static final Codec<WarpBindData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(WarpBindData::pos)
    ).apply(instance, WarpBindData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpBindData> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, WarpBindData::pos, WarpBindData::new
    );
}
