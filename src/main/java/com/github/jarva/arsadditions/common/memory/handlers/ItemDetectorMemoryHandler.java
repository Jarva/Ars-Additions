package com.github.jarva.arsadditions.common.memory.handlers;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.hollingsworth.arsnouveau.common.block.tile.ItemDetectorTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ItemDetectorMemoryHandler extends MemoryHandler {

    public ItemDetectorMemoryHandler() {
        super(ArsAdditions.prefix("item_detector"));
    }

    @Override
    public boolean canSaveFrom(BlockEntity blockEntity) {
        return blockEntity instanceof ItemDetectorTile;
    }

    @Override
    public boolean canLoadTo(BlockEntity blockEntity) {
        return blockEntity instanceof ItemDetectorTile;
    }

    @Override
    public CompoundTag save(BlockEntity blockEntity) {
        if (!(blockEntity instanceof ItemDetectorTile detector)) {
            return new CompoundTag();
        }

        CompoundTag data = detector.saveCustomOnly(detector.getLevel().registryAccess());
        data.remove("isPowered");
        return data;
    }

    @Override
    public void load(BlockEntity blockEntity, CompoundTag data, Player player) {
        if (!(blockEntity instanceof ItemDetectorTile detector)) {
            return;
        }

        detector.loadCustomOnly(data, detector.getLevel().registryAccess());
        detector.updateBlock();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("memory_handler.ars_additions.item_detector");
    }

    @Override
    public void getTooltip(CompoundTag data, List<Component> tooltip) {
        if (data.contains("filterStack")) {
            CompoundTag filterTag = data.getCompound("filterStack");
            if (filterTag.contains("id")) {
                ResourceLocation itemId = ResourceLocation.parse(filterTag.getString("id"));
                Component itemName = BuiltInRegistries.ITEM.get(itemId).getDescription();
                tooltip.add(Component.literal("  ").append(itemName).withStyle(ChatFormatting.AQUA));
            }
        }

        int neededCount = data.getInt("neededCount");
        boolean inverted = data.getBoolean("inverted");
        String comparison = inverted ? "< " : ">= ";
        tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
            Component.translatable("memory_handler.ars_additions.item_detector.count", comparison + neededCount))
            .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.ITEM_DETECTOR.get());
    }
}
