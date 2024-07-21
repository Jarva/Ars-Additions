package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PiglinBruteAi.class)
public class PiglinBruteAiMixin {
    @ModifyExpressionValue(method = "findNearestValidAttackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinBruteAi;getTargetIfWithinRange(Lnet/minecraft/world/entity/monster/piglin/AbstractPiglin;Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)Ljava/util/Optional;"))
    private static Optional<? extends LivingEntity> modifyTarget(Optional<? extends LivingEntity> opt) {
        return opt.filter(entity ->
                !CharmRegistry.processCharmEvent(entity, CharmRegistry.CharmType.GOLDEN,
                        () -> true,
                        (e, curio) -> CharmRegistry.every(20, entity, 10)
                )
        );
    }
}
