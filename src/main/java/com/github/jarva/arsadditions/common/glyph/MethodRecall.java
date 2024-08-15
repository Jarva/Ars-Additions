package com.github.jarva.arsadditions.common.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.UnstableReliquary;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonGlyphNames;
import com.github.jarva.arsadditions.common.item.data.mark.MarkData;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MethodRecall extends AbstractCastMethod {
    public static MethodRecall INSTANCE = new MethodRecall();

    private MethodRecall() {
        super(ArsAdditions.prefix(AddonGlyphNames.MethodRecall), "Recall");
    }

    @Override
    public String getBookDescription() {
        return "Recalls the target stored in a Reliquary and casts the spell on it.";
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return cast(context, playerEntity, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Player player = context.getPlayer();
        if (player == null) return CastResolveType.FAILURE;
        return cast(spellContext, player, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(spellContext, caster, resolver);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(spellContext, caster, resolver);
    }

    public CastResolveType cast(SpellContext context, LivingEntity caster, SpellResolver resolver) {
        ItemStack reliquary = UnstableReliquary.getReliquaryFromCaster(context, caster);
        if (reliquary == null) return CastResolveType.FAILURE;

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return CastResolveType.FAILURE;

        MarkData mark = reliquary.get(AddonDataComponentRegistry.MARK_DATA);
        if (mark == null) return CastResolveType.FAILURE;

        return mark.cast(context, reliquary, serverLevel, caster, resolver);
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.add(EffectMark.INSTANCE.getRegistryName());
    }

    @Override
    protected int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
