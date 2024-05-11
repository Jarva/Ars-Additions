package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.effect.MarkedEffect;
import com.github.jarva.arsadditions.setup.registry.names.AddonEffectNames;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonEffectRegistry {
    public static final List<RegistryObject<? extends MobEffect>> REGISTERED_EFFECTS = new ArrayList<>();
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> MARKED_EFFECT = EFFECTS.register(AddonEffectNames.MARKED, MarkedEffect::new);
}
