package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "angerNearbyPiglins", at = @At(value = "INVOKE", target = "Ljava/util/List;stream()Ljava/util/stream/Stream;", shift = At.Shift.BEFORE), cancellable = true)
    private static void angerNearbyPiglins(Player player, boolean angerOnlyIfCanSee, CallbackInfo ci, @Local List<Piglin> list) {
        CharmRegistry.processCharmEvent(player, CharmRegistry.CharmType.GOLDEN, () -> angerOnlyIfCanSee && !list.isEmpty(), (entity, curio) -> {
            ci.cancel();
            return 1;
        });
    }
}
