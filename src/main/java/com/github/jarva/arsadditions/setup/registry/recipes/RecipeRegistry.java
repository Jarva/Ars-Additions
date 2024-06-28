package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class RecipeRegistry<T extends Recipe<Container>> {
    private final RegistryObject<RecipeType<T>> type;
    private final Predicate<T> predicate;

    public RecipeRegistry(RegistryObject<RecipeType<T>> recipeType, Predicate<T> predicate) {
        this.type = recipeType;
        this.predicate = predicate;
    }

    public List<T> RECIPES = new ArrayList<>();

    public List<T> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public void reload(RecipeManager recipeManager) {
        RECIPES.clear();
        List<T> recipes = recipeManager.getAllRecipesFor(type.get());
        for (T recipe : recipes) {
            if (predicate.test(recipe)) {
                RECIPES.add(recipe);
            }
        }
    }

    public static void reloadAll(RecipeManager recipeManager) {
        for (RecipeRegistry<?> registry : AddonRecipeRegistry.RECIPE_REGISTRY) {
            registry.reload(recipeManager);
        }
    }
}
