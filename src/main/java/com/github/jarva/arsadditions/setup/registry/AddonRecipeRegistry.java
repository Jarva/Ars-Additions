package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.setup.registry.recipes.RecipeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonRecipeRegistry {
    public static final List<RecipeRegistry<?>> RECIPE_REGISTRY = new ArrayList<>();
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final String SOURCE_SPAWNER_RECIPE_ID = "source_spawner";
    public static final String LOCATE_STRUCTURE_RECIPE_ID = "locate_structure";

    public static final RegistryObject<RecipeType<SourceSpawnerRecipe>> SOURCE_SPAWNER_TYPE = RECIPE_TYPES.register(SOURCE_SPAWNER_RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<SourceSpawnerRecipe>> SOURCE_SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register(SOURCE_SPAWNER_RECIPE_ID, SourceSpawnerRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<LocateStructureRecipe>> LOCATE_STRUCTURE_TYPE = RECIPE_TYPES.register(LOCATE_STRUCTURE_RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<LocateStructureRecipe>> LOCATE_STRUCTURE_SERIALIZER = RECIPE_SERIALIZERS.register(LOCATE_STRUCTURE_RECIPE_ID, LocateStructureRecipe.Serializer::new);

    public static final RecipeRegistry<LocateStructureRecipe> LOCATE_STRUCTURE_REGISTRY = createRegistry(LOCATE_STRUCTURE_TYPE);
    public static List<SourceSpawnerRecipe> DEFAULTS = new ArrayList<>();
    public static final RecipeRegistry<SourceSpawnerRecipe> SOURCE_SPAWNER_REGISTRY = createRegistry(SOURCE_SPAWNER_TYPE, (recipe) -> {
        if (recipe.entity().isEmpty()) {
            DEFAULTS.add(recipe);
            return false;
        }
        return true;
    });

    public static <T extends Recipe<Container>> RecipeRegistry<T> createRegistry(RegistryObject<RecipeType<T>> recipeType) {
        return createRegistry(recipeType, $ -> true);
    }

    public static <T extends Recipe<Container>> RecipeRegistry<T> createRegistry(RegistryObject<RecipeType<T>> recipeType, Predicate<T> predicate) {
        RecipeRegistry<T> registry = new RecipeRegistry<>(recipeType, predicate);
        RECIPE_REGISTRY.add(registry);
        return registry;
    }

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return ForgeRegistries.RECIPE_TYPES.getKey(this).toString();
        }
    }
}

