package com.github.jarva.arsadditions.common.memory.handlers;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class SpellSensorMemoryHandler extends MemoryHandler {

    public SpellSensorMemoryHandler() {
        super(ArsAdditions.prefix("spell_sensor"));
    }

    @Override
    public boolean canSaveFrom(BlockEntity blockEntity) {
        return blockEntity instanceof SpellSensorTile;
    }

    @Override
    public boolean canLoadTo(BlockEntity blockEntity) {
        return blockEntity instanceof SpellSensorTile;
    }

    @Override
    public CompoundTag save(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SpellSensorTile sensor)) {
            return new CompoundTag();
        }

        return sensor.saveCustomOnly(sensor.getLevel().registryAccess());
    }

    @Override
    public void load(BlockEntity blockEntity, CompoundTag data, Player player) {
        if (!(blockEntity instanceof SpellSensorTile sensor)) {
            return;
        }

        sensor.loadCustomOnly(data, sensor.getLevel().registryAccess());
        sensor.updateBlock();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("memory_handler.ars_additions.spell_sensor");
    }

    @Override
    public void getTooltip(CompoundTag data, List<Component> tooltip) {
        boolean isOnResolve = data.getBoolean("isOnResolve");
        tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
            Component.translatable(isOnResolve
                ? "memory_handler.ars_additions.spell_sensor.on_resolve"
                : "memory_handler.ars_additions.spell_sensor.on_cast")
        ).withStyle(ChatFormatting.GRAY));

        int outputStrength = data.getInt("outputStrength");
        if (outputStrength > 0) {
            tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
                Component.translatable("memory_handler.ars_additions.spell_sensor.strength", outputStrength))
                .withStyle(ChatFormatting.GRAY));
        }

        if (data.contains("parchment")) {
            tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
                Component.translatable("memory_handler.ars_additions.spell_sensor.has_filter"))
                .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.SPELL_SENSOR.get());
    }
}
