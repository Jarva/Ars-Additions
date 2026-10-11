package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.common.util.RetaliationTargets;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapMethod(method = "hurt")
    private boolean arsAdditions$trackDamage(DamageSource source, float amount, Operation<Boolean> original) {
        return RetaliationTargets.during((LivingEntity) (Object) this, source, () -> original.call(source, amount));
    }
}
