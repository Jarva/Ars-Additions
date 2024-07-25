package com.github.jarva.arsadditions.common.util.codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;

public interface TagModifier {
    MapCodec<? extends TagModifier> type();
    void modify(CompoundTag nbt);
}
