package com.github.jarva.arsadditions.common.memory.handlers;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class RuneMemoryHandler extends MemoryHandler {

    public RuneMemoryHandler() {
        super(ArsAdditions.prefix("rune"));
    }

    @Override
    public boolean canSaveFrom(BlockEntity blockEntity) {
        return blockEntity instanceof RuneTile;
    }

    @Override
    public boolean canLoadTo(BlockEntity blockEntity) {
        return blockEntity instanceof RuneTile;
    }

    @Override
    public CompoundTag save(BlockEntity blockEntity) {
        if (!(blockEntity instanceof RuneTile rune)) {
            return new CompoundTag();
        }

        CompoundTag data = rune.saveCustomOnly(rune.getLevel().registryAccess());
        data.remove("uuid");
        data.remove("charged");
        data.remove("redstone");
        return data;
    }

    @Override
    public void load(BlockEntity blockEntity, CompoundTag data, Player player) {
        if (!(blockEntity instanceof RuneTile rune)) {
            return;
        }

        data.putUUID("uuid", player.getUUID());
        rune.loadCustomOnly(data, rune.getLevel().registryAccess());
        rune.updateBlock();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("memory_handler.ars_additions.rune");
    }

    @Override
    public void getTooltip(CompoundTag data, List<Component> tooltip) {
        if (!data.contains("spell")) {
            return;
        }

        Spell spell = ANCodecs.decode(Spell.CODEC.codec(), data.get("spell"));
        if (spell != null && !spell.isEmpty()) {
            tooltip.add(Component.literal("  " + spell.getDisplayString()).withStyle(ChatFormatting.GRAY));
        }

        if (data.getBoolean("sensitive")) {
            tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
                Component.translatable("memory_handler.ars_additions.rune.sensitive")).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.RUNE_BLOCK.get());
    }
}
