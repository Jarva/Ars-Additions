package com.github.jarva.arsadditions.datagen.recipes;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.imbuement.BulkScribingRecipe;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BulkScribingProvider extends SimpleDataProvider {
    public List<BulkScribingRecipe> recipes = new ArrayList<>();

    public BulkScribingProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (BulkScribingRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }

    protected void addEntries() {
        addEntry("bulk_scriber");
    }

    private void addEntry(String id) {
        recipes.add(new BulkScribingRecipe(ArsAdditions.prefix(id)));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipes/bulk_scribing/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Bulk Scribing Datagen";
    }
}
