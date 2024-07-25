package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.effect.MarkedEffect;
import com.github.jarva.arsadditions.setup.registry.names.AddonEffectNames;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonEffectRegistry {
    public static final List<DeferredHolder<MobEffect, ? extends MobEffect>> REGISTERED_EFFECTS = new ArrayList<>();
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

    public static final DeferredHolder<MobEffect, MarkedEffect> MARKED_EFFECT = EFFECTS.register(AddonEffectNames.MARKED, MarkedEffect::new);
}
