package com.github.jarva.arsadditions.common.advancement;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Triggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, ArsAdditions.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FIND_RUINED_PORTAL = register("find_ruined_portal");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CREATE_RUINED_PORTAL = register("create_ruined_portal");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> WIXIE_ENCHANTING_APPARATUS = register("wixie_enchanting_apparatus");

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String name) {
        return register(name, new PlayerTrigger());
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String name, T trigger) {
        return TRIGGERS.register(name, () -> trigger);
    }

    public static void init() {}
}
