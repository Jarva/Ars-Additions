package com.github.jarva.arsadditions.common.memory.handlers;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class TurretMemoryHandler extends MemoryHandler {

    public TurretMemoryHandler() {
        super(ArsAdditions.prefix("spell_turret"));
    }

    @Override
    public boolean canSaveFrom(BlockEntity blockEntity) {
        return blockEntity instanceof BasicSpellTurretTile;
    }

    @Override
    public boolean canLoadTo(BlockEntity blockEntity) {
        return blockEntity instanceof BasicSpellTurretTile;
    }

    @Override
    public CompoundTag save(BlockEntity blockEntity) {
        if (!(blockEntity instanceof BasicSpellTurretTile turret)) {
            return new CompoundTag();
        }

        CompoundTag data = turret.saveCustomOnly(turret.getLevel().registryAccess());
        data.remove("uuid");
        return data;
    }

    @Override
    public void load(BlockEntity blockEntity, CompoundTag data, Player player) {
        if (!(blockEntity instanceof BasicSpellTurretTile turret)) {
            return;
        }

        data.putUUID("uuid", player.getUUID());
        turret.loadCustomOnly(data, turret.getLevel().registryAccess());
        turret.updateBlock();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("memory_handler.ars_additions.spell_turret");
    }

    @Override
    public void getTooltip(CompoundTag data, List<Component> tooltip) {
        if (!data.contains("spell_caster")) {
            return;
        }

        SpellCaster caster = ANCodecs.decode(SpellCaster.CODEC.codec(), data.get("spell_caster"));
        if (caster == null) return;

        if (!caster.getSpellName().isEmpty()) {
            tooltip.add(Component.literal("  " + caster.getSpellName()).withStyle(ChatFormatting.AQUA));
        }

        Spell spell = caster.getSpell();
        if (spell != null && !spell.isEmpty()) {
            tooltip.add(Component.literal("  " + spell.getDisplayString()).withStyle(ChatFormatting.GRAY));
        }

        if (data.contains("time")) {
            int ticks = data.getInt("time");
            double seconds = ticks / 20.0;
            tooltip.add(Component.translatable("memory_handler.ars_additions.detail",
                Component.translatable("memory_handler.ars_additions.spell_turret.delay", String.format("%.1f", seconds)))
                .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.BASIC_SPELL_TURRET.get());
    }
}
