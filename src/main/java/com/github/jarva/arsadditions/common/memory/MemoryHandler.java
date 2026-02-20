package com.github.jarva.arsadditions.common.memory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/**
 * Base class for handling save/load operations for specific block or entity types.
 * Extend this class to add Memory Crystal support for custom blocks or entities.
 */
public abstract class MemoryHandler {

    private final ResourceLocation id;

    protected MemoryHandler(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean canSaveFrom(BlockEntity blockEntity) {
        return false;
    }

    public boolean canSaveFrom(Entity entity) {
        return false;
    }

    public boolean canLoadTo(BlockEntity blockEntity) {
        return false;
    }

    public boolean canLoadTo(Entity entity) {
        return false;
    }

    public CompoundTag save(BlockEntity blockEntity) {
        throw new UnsupportedOperationException(
            "Handler " + id + " does not support block entities"
        );
    }

    public CompoundTag save(Entity entity) {
        throw new UnsupportedOperationException(
            "Handler " + id + " does not support entities"
        );
    }

    public void load(BlockEntity blockEntity, CompoundTag data, Player player) {
        throw new UnsupportedOperationException(
            "Handler " + id + " does not support block entities"
        );
    }

    public void load(Entity entity, CompoundTag data, Player player) {
        throw new UnsupportedOperationException(
            "Handler " + id + " does not support entities"
        );
    }

    public abstract Component getDisplayName();

    public void getTooltip(CompoundTag data, List<Component> tooltip) {
    }

    public ItemStack getIcon() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }
}
