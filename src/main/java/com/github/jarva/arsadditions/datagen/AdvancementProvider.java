package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.advancement.Triggers;
import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends ForgeAdvancementProvider {
    public AdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new Advancements()));
    }

    public static class Advancements implements ForgeAdvancementProvider.AdvancementGenerator {
        static Advancement dummy(String name) {
            return new Advancement(new ResourceLocation(ArsNouveau.MODID, name), null, null, AdvancementRewards.EMPTY, ImmutableMap.of(), null, false);
        }

        @Override
        public void generate(HolderLookup.Provider arg, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
            Advancement findRuinedPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "find_ruined_portal")
                    .display(BlockRegistry.getBlock(LibBlockNames.GILDED_SOURCESTONE_LARGE_BRICKS), FrameType.TASK)
                    .addCriterion(new PlayerTrigger.TriggerInstance(Triggers.FIND_RUINED_PORTAL.getId(), ContextAwarePredicate.ANY))
                    .parent(dummy("root"))
                    .save(consumer);

            Advancement createWarpPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "create_ruined_portal")
                    .display(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get(), FrameType.CHALLENGE, true)
                    .addCriterion(new PlayerTrigger.TriggerInstance(Triggers.CREATE_RUINED_PORTAL.getId(), ContextAwarePredicate.ANY))
                    .parent(findRuinedPortal)
                    .save(consumer);
        }
    }
}
