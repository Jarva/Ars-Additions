package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class SourceSpawnerRegistry extends GenericRecipeRegistry<Container, SourceSpawnerRecipe> {
    public static SourceSpawnerRegistry INSTANCE = new SourceSpawnerRegistry();

    public SourceSpawnerRegistry() {
        super(AddonRecipeRegistry.SOURCE_SPAWNER_TYPE);
    }

    public List<SourceSpawnerRecipe> DEFAULTS = new ArrayList<>();

    @Override
    public void reload(RecipeManager manager) {
        RECIPES.clear();
        List<? extends SourceSpawnerRecipe> recipes = manager.getAllRecipesFor(getType());
        for (SourceSpawnerRecipe recipe : recipes) {
            if (recipe.entity().isEmpty()) {
                DEFAULTS.add(recipe);
                continue;
            }
            RECIPES.add(recipe);
        }
    }
}
