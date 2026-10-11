package com.github.jarva.arsadditions.common.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

/** Damage currently being processed, before vanilla updates attacker history. */
public final class RetaliationTargets {
    private record Frame(LivingEntity victim, DamageSource source, Frame parent) {}

    private static final ThreadLocal<Frame> CURRENT = new ThreadLocal<>();

    private RetaliationTargets() {}

    public static LivingEntity getAttacker(LivingEntity victim) {
        for (var frame = CURRENT.get(); frame != null; frame = frame.parent()) {
            if (frame.victim() == victim) {
                // An active hit without a living attacker must not use old history.
                return frame.source().getEntity() instanceof LivingEntity attacker ? attacker : null;
            }
        }
        return victim.getKillCredit();
    }

    public static <T> T during(LivingEntity victim, DamageSource source, Supplier<T> action) {
        var previous = CURRENT.get();
        CURRENT.set(new Frame(victim, source, previous));
        try {
            return action.get();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
