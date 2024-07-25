package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;

public class ImbuementDatagen extends ImbuementRecipeProvider {
    public ImbuementDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/imbuement/" + str + ".json");
    }

    public void addEntries() {
        for (CharmRegistry.CharmType value : CharmRegistry.CharmType.values()) {
            recipes.add(new ImbuementRecipe(
                    ArsAdditions.prefix(value.getSerializedName()).withPrefix("charms/"),
                    Ingredient.of(AddonItemRegistry.CHARMS.get(value).get()),
                    AddonItemRegistry.CHARMS.get(value).get().getDefaultInstance(),
                    2000)
            );
        }
    }
}
