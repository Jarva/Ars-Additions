package com.github.jarva.arsadditions.client.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CompassUtil implements CompassItemPropertyFunction.CompassTarget {
    @Nullable
    @Override
    public GlobalPos getPos(ClientLevel level, ItemStack stack, Entity entity) {
        return GlobalPos.CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().getCompound("Locator")).result().orElse(null);
    }
}
