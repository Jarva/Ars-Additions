package com.github.jarva.arsadditions.datagen.recipes;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.imbuement.ImbueSpellScrollRecipe;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImbueSpellScrollProvider extends SimpleDataProvider {
    public List<ImbueSpellScrollRecipe> recipes = new ArrayList<>();

    public ImbueSpellScrollProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (ImbueSpellScrollRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.getId().getPath());
            saveStable(pOutput, recipe.asRecipe(), path);
        }
    }

    protected void addEntries() {
        addEntry("imbue_scroll");
    }

    private void addEntry(String id) {
        recipes.add(new ImbueSpellScrollRecipe(ArsAdditions.prefix(id)));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipes/imbue_spell_scroll/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Imbue Spell Scroll Datagen";
    }
}
