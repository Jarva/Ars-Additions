package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.common.glyph.MethodRetaliate;
import com.github.jarva.arsadditions.common.util.RetaliationTargets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("ars_additions")
@PrefixGameTestTemplate(false)
public final class RetaliateGameTests {
    private static final class DamageProbe extends Cow {
        Runnable callback = () -> {};

        DamageProbe(Level level) {
            super(EntityType.COW, level);
            setNoAi(true);
            setNoGravity(true);
        }

        @Override
        protected void actuallyHurt(DamageSource source, float amount) {
            super.actuallyHurt(source, amount);
            // Reactive runs here, before hurt() updates vanilla attacker history.
            callback.run();
        }
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void retaliationUsesCurrentAttacker(GameTestHelper helper) {
        var victim = new DamageProbe(helper.getLevel());
        victim.setPos(helper.absoluteVec(new Vec3(3, 4, 3)));
        helper.getLevel().addFreshEntity(victim);
        var first = helper.spawn(EntityType.COW, 6, 4, 3);
        var second = helper.spawn(EntityType.COW, 8, 4, 3);
        first.setNoAi(true);
        second.setNoAi(true);
        LivingEntity[] selected = {null};
        var resolver = new SpellResolver(new SpellContext(helper.getLevel(),
                new Spell(MethodRetaliate.INSTANCE), victim, LivingCaster.from(victim), ItemStack.EMPTY)) {
            @Override
            public void onResolveEffect(Level level, HitResult hit) {
                selected[0] = (LivingEntity) ((EntityHitResult) hit).getEntity();
            }
        };
        victim.callback = () -> {
            selected[0] = null;
            MethodRetaliate.INSTANCE.cast(victim, resolver);
        };
        try {
            victim.hurt(victim.damageSources().mobAttack(first), 1);
            helper.assertTrue(selected[0] == first, "The first hit must retaliate against its attacker");
            victim.invulnerableTime = 0;
            victim.hurt(victim.damageSources().mobAttack(second), 1);
            helper.assertTrue(selected[0] == second, "Alternating attackers must not use the previous hit");
            victim.invulnerableTime = 0;
            victim.hurt(victim.damageSources().magic(), 1);
            helper.assertTrue(selected[0] == null, "Unattributed damage must not target an old attacker");
            selected[0] = null;
            MethodRetaliate.INSTANCE.cast(victim, resolver);
            helper.assertTrue(selected[0] == second, "Manual casts must retain vanilla history");

            RetaliationTargets.during(victim, victim.damageSources().mobAttack(first), () -> {
                try {
                    RetaliationTargets.during(victim, victim.damageSources().mobAttack(second), () -> {
                        throw new IllegalStateException("Nested damage test");
                    });
                } catch (IllegalStateException expected) {
                    helper.assertTrue(RetaliationTargets.getAttacker(victim) == first,
                            "Exceptions must restore the outer damage context");
                }
                return null;
            });
            helper.assertTrue(RetaliationTargets.getAttacker(victim) == second,
                    "Completed scopes must leave only vanilla history");
        } finally {
            victim.discard();
        }
        helper.succeed();
    }
}
