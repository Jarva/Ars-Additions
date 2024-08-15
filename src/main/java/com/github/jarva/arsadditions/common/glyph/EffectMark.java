package com.github.jarva.arsadditions.common.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.UnstableReliquary;
import com.github.jarva.arsadditions.common.item.data.mark.EntityMarkData;
import com.github.jarva.arsadditions.common.item.data.mark.LocationMarkData;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonEffectRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonGlyphNames;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
        ItemStack reliquary = UnstableReliquary.getReliquaryFromCaster(spellContext, shooter);
        if (reliquary == null) return;

        BlockPos pos = rayTraceResult.getBlockPos();

        reliquary.set(AddonDataComponentRegistry.MARK_DATA, new LocationMarkData(new GlobalPos(world.dimension(), pos)));
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ItemStack reliquary = UnstableReliquary.getReliquaryFromCaster(spellContext, shooter);
        if (reliquary == null)
            return;

        Entity entity = rayTraceResult.getEntity();
        Optional<Component> name = Optional.empty();

        if (entity instanceof Player player) {
            name = Optional.ofNullable(player.getDisplayName());
            player.addEffect(new MobEffectInstance(AddonEffectRegistry.MARKED_EFFECT, ServerConfig.SERVER.reliquary_effect_duration.get() * 20));
        }

        reliquary.set(AddonDataComponentRegistry.MARK_DATA, new EntityMarkData(entity.getUUID(), entity.getType().builtInRegistryHolder(), name));
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
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
