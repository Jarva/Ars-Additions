package com.github.jarva.arsadditions.datagen;

import com.hollingsworth.arsnouveau.common.datagen.RecipeDatagen;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class RecipeProvider extends RecipeDatagen {
    public RecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        this.consumer = consumer;
    }
}
