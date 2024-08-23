package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class SourceSpawnerRegistry extends GenericRecipeRegistry<RecipeInput, SourceSpawnerRecipe> {
    public static SourceSpawnerRegistry INSTANCE;

    public SourceSpawnerRegistry() {
        super(AddonRecipeRegistry.SOURCE_SPAWNER_TYPE);
    }

    public List<RecipeHolder<? extends SourceSpawnerRecipe>> DEFAULTS = new ArrayList<>();

    @Override
    public void reload(RecipeManager manager) {
        RECIPES.clear();
        var recipes = manager.getAllRecipesFor(getType());
        for (RecipeHolder<? extends SourceSpawnerRecipe> recipe : recipes) {
            if (recipe.value().entity().isEmpty()) {
                DEFAULTS.add(recipe);
                continue;
            }
            RECIPES.add(recipe);
        }
    }
}
