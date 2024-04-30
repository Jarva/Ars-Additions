package com.github.jarva.arsadditions.common.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.names.AddonGlyphNames;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MethodRetaliate extends AbstractCastMethod {
    public static MethodRetaliate INSTANCE = new MethodRetaliate();

    private MethodRetaliate() {
        super(ArsAdditions.prefix(AddonGlyphNames.MethodRetaliate), "Retaliate");
    }

    @Override
    public String getBookDescription() {
        return "Applies spells to the last entity that dealt damage to you. The damage must be within the last 5 seconds.";
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return cast(playerEntity, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Player player = context.getPlayer();
        if (player == null) return CastResolveType.FAILURE;
        return cast(player, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(caster, resolver);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(caster, resolver);
    }

    public CastResolveType cast(LivingEntity caster, SpellResolver resolver) {
        LivingEntity lastHurtBy = caster.getKillCredit();
        if (lastHurtBy == null) return CastResolveType.FAILURE;

        resolver.onResolveEffect(caster.getLevel(), new EntityHitResult(lastHurtBy));
        return CastResolveType.SUCCESS;
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
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
