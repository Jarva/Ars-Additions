package com.github.jarva.arsadditions.advancement;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;

public class Triggers {
    public static final PlayerTrigger FIND_RUINED_PORTAL = register(new PlayerTrigger(new ResourceLocation(ArsAdditions.MODID, "find_ruined_portal")));
    public static final PlayerTrigger CREATE_RUINED_PORTAL = register(new PlayerTrigger(new ResourceLocation(ArsAdditions.MODID, "create_ruined_portal")));

    public static <T extends CriterionTrigger<?>> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }
}
