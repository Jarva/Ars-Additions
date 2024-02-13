package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.registry.ModRegistry;
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
        this.addRecipe(this.builder().withResult(ModRegistry.LECTERN_REMOTE)
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(1, ItemsRegistry.SCRY_CASTER)
                .withPedestalItem(1, BlockRegistry.SCRYERS_CRYSTAL)
                .withPedestalItem(1, ItemsRegistry.STARBUNCLE_CHARM)
                .withPedestalItem(1, ItemsRegistry.BOOKWYRM_CHARM)
                .build());
        this.addRecipe(this.builder().withResult(ModRegistry.ADVANCED_LECTERN_REMOTE)
                .withReagent(ModRegistry.LECTERN_REMOTE)
                .withPedestalItem(1, Items.NETHERITE_INGOT)
                .withPedestalItem(1, Items.NETHER_STAR)
                .withPedestalItem(1, Blocks.ENDER_CHEST)
                .build());
    }
}
