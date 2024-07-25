package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancementBuilder;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementDatagen extends AdvancementProvider {
    public AdvancementDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new Advancements()));
    }

    public static class Advancements implements AdvancementGenerator {
        static AdvancementHolder dummy(String name) {
            return new AdvancementHolder(ArsNouveau.prefix(name), new Advancement(Optional.empty(), Optional.empty(), AdvancementRewards.EMPTY, Map.of(), AdvancementRequirements.EMPTY, false, Optional.empty()));
        }

        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
            AdvancementHolder findRuinedPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "find_ruined_portal")
                    .display(BlockRegistry.getBlock(LibBlockNames.GILDED_SOURCESTONE_LARGE_BRICKS), AdvancementType.TASK)
                    .addCriterion(new Criterion<>(Triggers.FIND_RUINED_PORTAL.get(), new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .parent(dummy("root"))
                    .save(consumer);

            AdvancementHolder createWarpPortal = ANAdvancementBuilder.builder(ArsAdditions.MODID, "create_ruined_portal")
                    .display(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get(), AdvancementType.CHALLENGE, true)
                    .addCriterion(new Criterion<>(Triggers.CREATE_RUINED_PORTAL.get(), new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .parent(findRuinedPortal)
                    .save(consumer);
        }
    }
}
