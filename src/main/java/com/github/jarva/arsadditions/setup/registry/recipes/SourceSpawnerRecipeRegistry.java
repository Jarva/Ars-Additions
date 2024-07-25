package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class SourceSpawnerRecipeRegistry extends GenericRecipeRegistry<RecipeInput, SourceSpawnerRecipe> {
    public static List<RecipeHolder<? extends SourceSpawnerRecipe>> DEFAULTS = new ArrayList<>();

    public SourceSpawnerRecipeRegistry() {
        super(AddonRecipeRegistry.SOURCE_SPAWNER_TYPE);
    }

    @Override
    public void reload(RecipeManager manager) {
        RECIPES.clear();
        List<? extends RecipeHolder<? extends SourceSpawnerRecipe>> recipes = manager.getAllRecipesFor(getType());
        for (RecipeHolder<? extends SourceSpawnerRecipe> recipe : recipes) {
            if (recipe.value().entity().isEmpty()) {
                DEFAULTS.add(recipe);
                continue;
            }
            RECIPES.add(recipe);
        }
    }
}
