package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBlink;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectBlink.class)
public class EffectBlinkMixin {
    @Inject(method = "onResolveEntity", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/spell/effect/EffectBlink;warpEntity(Lnet/minecraft/world/entity/Entity;Lcom/hollingsworth/arsnouveau/common/items/WarpScroll$WarpScrollData;)V", shift = At.Shift.BEFORE), cancellable = true, remap = false)
    private void dontWarpExplorer(EntityHitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, CallbackInfo ci) {
        if (shooter.getOffhandItem().is(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get())) {
            ci.cancel();
        }
    }
}
