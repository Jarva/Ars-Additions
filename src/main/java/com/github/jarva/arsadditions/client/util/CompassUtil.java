package com.github.jarva.arsadditions.client.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompassUtil implements CompassItemPropertyFunction.CompassTarget {
    @Nullable
    @Override
    public GlobalPos getPos(ClientLevel level, ItemStack stack, Entity entity) {
        return stack.getOrDefault(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), false)).target().orElse(null);
    }
}
