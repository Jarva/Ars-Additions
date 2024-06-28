package com.github.jarva.arsadditions.common.util.codec;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

public interface TagModifier {
    Codec<? extends TagModifier> type();
    void modify(CompoundTag nbt);
}
