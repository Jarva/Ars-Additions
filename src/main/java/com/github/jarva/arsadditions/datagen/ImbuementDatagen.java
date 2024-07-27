package com.github.jarva.arsadditions.datagen;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.nio.file.Path;

public class ImbuementDatagen extends ImbuementRecipeProvider {
    public ImbuementDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/imbuement/" + str + ".json");
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        addEntries();
        for (ImbuementRecipe g : recipes) {
            Path path = getRecipePath(output, g.id.getPath());
            saveStable(cache, g.asRecipe(), path);
        }
    }

    public void addEntries() {
//        for (CharmRegistry.CharmType value : CharmRegistry.CharmType.values()) {
//            recipes.add(new ImbuementRecipe(ArsAdditions.prefix(value.getSerializedName()).withPrefix("charms/"), Ingredient.of(AddonItemRegistry.CHARMS.get(value).get()), AddonItemRegistry.CHARMS.get(value).get().getDefaultInstance(), 2000, List.of()));
//        }
    }
}
