package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class AdvancementDatagen extends AdvancementProvider {
    public AdvancementDatagen(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, existingFileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        new Advancements().accept(consumer);
    }

    public static class Advancements implements Consumer<Consumer<Advancement>> {
        static Advancement dummy(String name) {
            return new Advancement(new ResourceLocation(ArsNouveau.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null);
        }

        @Override
        public void accept(Consumer<Advancement> consumer) {
            Advancement findRuinedPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "find_ruined_portal")
                    .display(BlockRegistry.getBlock(LibBlockNames.GILDED_SOURCESTONE_LARGE_BRICKS), FrameType.TASK)
                    .addCriterion(new PlayerTrigger.TriggerInstance(Triggers.FIND_RUINED_PORTAL.getId(), EntityPredicate.Composite.ANY))
                    .parent(dummy("root"))
                    .save(consumer);

            Advancement createWarpPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "create_ruined_portal")
                    .display(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get(), FrameType.CHALLENGE, true)
                    .addCriterion(new PlayerTrigger.TriggerInstance(Triggers.CREATE_RUINED_PORTAL.getId(), EntityPredicate.Composite.ANY))
                    .parent(findRuinedPortal)
                    .save(consumer);
        }
    }
}
