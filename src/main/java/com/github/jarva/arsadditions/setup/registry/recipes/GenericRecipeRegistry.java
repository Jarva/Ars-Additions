package com.github.jarva.arsadditions.setup.registry.recipes;

import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericRecipeRegistry<C extends RecipeInput, T extends Recipe<C>> {
    private final DeferredHolder<? extends RecipeType<?>, ? extends RecipeType<? extends T>> type;

    public GenericRecipeRegistry(DeferredHolder<? extends RecipeType<?>, ? extends RecipeType<? extends T>> type) {
        this.type = type;
        REGISTRIES.add(this);
    }

    public List<RecipeHolder<? extends T>> RECIPES = new ArrayList<>();

    public List<RecipeHolder<? extends T>> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public RecipeType<? extends T> getType() {
        return type.get();
    }

    public void reload(RecipeManager manager) {
        RECIPES.clear();
        List<? extends RecipeHolder<? extends T>> recipes = manager.getAllRecipesFor(getType());
        RECIPES.addAll(recipes);
    }

    public static List<GenericRecipeRegistry<?, ?>> REGISTRIES = new ArrayList<>();

    public static void reloadAll(RecipeManager recipeManager) {
        for (GenericRecipeRegistry<?, ?> registry : REGISTRIES) {
            registry.reload(recipeManager);
        }
    }
}
