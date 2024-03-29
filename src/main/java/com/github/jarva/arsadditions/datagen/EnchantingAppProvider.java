package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;

public class EnchantingAppProvider extends ApparatusRecipeProvider {
    public EnchantingAppProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/" + str + ".json");
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        addEntries();
        for (EnchantingApparatusRecipe g : recipes) {
            Path path = getRecipePath(output, g.getId().getPath());
            saveStable(cache, g.asRecipe(), path);
        }
    }

    @Override
    public void addEntries() {
        this.addRecipe(this.builder().withResult(AddonItemRegistry.LECTERN_REMOTE)
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(1, ItemsRegistry.SCRY_CASTER)
                .withPedestalItem(1, BlockRegistry.SCRYERS_CRYSTAL)
                .withPedestalItem(1, ItemsRegistry.STARBUNCLE_CHARM)
                .withPedestalItem(1, ItemsRegistry.BOOKWYRM_CHARM)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonItemRegistry.ADVANCED_LECTERN_REMOTE)
                .withReagent(AddonItemRegistry.LECTERN_REMOTE)
                .withPedestalItem(1, Items.NETHERITE_INGOT)
                .withPedestalItem(1, Items.NETHER_STAR)
                .withPedestalItem(1, Blocks.ENDER_CHEST)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonBlockRegistry.ENDER_SOURCE_JAR)
                .withReagent(BlockRegistry.SOURCE_JAR)
                .withPedestalItem(4, Items.ENDER_PEARL)
                .withPedestalItem(4, Items.POPPED_CHORUS_FRUIT)
                .build()
        );
    }
}
