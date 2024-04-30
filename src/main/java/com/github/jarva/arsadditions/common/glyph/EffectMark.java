package com.github.jarva.arsadditions.common.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.UnstableReliquary;
import com.github.jarva.arsadditions.setup.registry.AddonEffectRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonGlyphNames;
import com.github.jarva.arsadditions.server.util.MarkType;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

        saveMark(spellContext, shooter, MarkType.LOCATION, data);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        CompoundTag data = new CompoundTag();

        Entity entity = rayTraceResult.getEntity();
        data.putUUID("entity_uuid", entity.getUUID());
        data.putString("entity_type", EntityType.getKey(entity.getType()).toString());
        if (entity.hasCustomName()) {
            data.putString("entity_name", Component.Serializer.toJson(entity.getCustomName()));
        }

        boolean marked = saveMark(spellContext, shooter, MarkType.ENTITY, data);

        if (marked && entity instanceof Player player) {
            data.putString("entity_name", Component.Serializer.toJson(player.getDisplayName()));
            player.addEffect(new MobEffectInstance(AddonEffectRegistry.MARKED_EFFECT.get(), 60 * 20 * 5));
        }
    }

    private boolean saveMark(SpellContext context, LivingEntity caster, MarkType type, CompoundTag tag) {
        ItemStack reliquary = UnstableReliquary.getReliquaryFromCaster(context, caster);
        if (reliquary == null) return false;

        CompoundTag itemTag = reliquary.getOrCreateTag();

        itemTag.putString("mark_type", type.name());
        itemTag.put("mark_data", tag);

        return true;
    }

    @Override
    public int getDefaultManaCost() {
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
