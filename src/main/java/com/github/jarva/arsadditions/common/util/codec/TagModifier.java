package com.github.jarva.arsadditions.common.util.codec;

import com.github.jarva.arsadditions.setup.registry.ModifyTagRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;

public interface TagModifier {
    Codec<TagModifier> CODEC = ModifyTagRegistry.TAG_MODIFIER_REGISTRY.byNameCodec().dispatch(TagModifier::type, Function.identity());

    MapCodec<? extends TagModifier> type();
    void modify(CompoundTag nbt);
}
