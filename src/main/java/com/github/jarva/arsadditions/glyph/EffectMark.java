package com.github.jarva.arsadditions.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.registry.names.AddonGlyphNames;
import com.github.jarva.arsadditions.util.MarkType;
import com.github.jarva.arsadditions.util.MarkUtils;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectMark extends AbstractEffect {
    public static EffectMark INSTANCE = new EffectMark();

    public EffectMark() {
        super(ArsAdditions.prefix(AddonGlyphNames.EffectMark), "Mark");
    }

    @Override
    public String getBookDescription() {
        return "Marks the target and stores the mark in a Reliquary.";
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        CompoundTag data = new CompoundTag();

        BlockPos pos = rayTraceResult.getBlockPos();
        data.put("block_pos", NbtUtils.writeBlockPos(pos));
        data.putString("block_dimension", world.dimension().location().toString());

        saveMark(shooter, MarkType.LOCATION, data);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        CompoundTag data = new CompoundTag();

        Entity entity = rayTraceResult.getEntity();
        data.putUUID("entity_uuid", entity.getUUID());
        data.putString("entity_type", entity.getType().getDescriptionId());

        saveMark(shooter, MarkType.ENTITY, data);
    }

    private void saveMark(LivingEntity caster, MarkType type, CompoundTag tag) {
        ItemStack reliquary = MarkUtils.getReliquaryFromCaster(caster);
        if (reliquary == null) return;

        MarkType mark = MarkUtils.getMarkType(reliquary);
        if (mark == MarkType.ENTITY || mark == MarkType.LOCATION) return;

        CompoundTag itemTag = reliquary.getOrCreateTag();

        itemTag.putString("mark_type", type.name());
        itemTag.put("mark_data", tag);
    }

    @Override
    protected int getDefaultManaCost() {
        return 25;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
