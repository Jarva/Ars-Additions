package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final String SOURCE_SPAWNER_RECIPE_ID = "armor_upgrade";

    public static final RegistryObject<RecipeType<SourceSpawnerRecipe>> SOURCE_SPAWNER_TYPE = RECIPE_TYPES.register(SOURCE_SPAWNER_RECIPE_ID, ModRecipeType::new);
    public static final RegistryObject<RecipeSerializer<SourceSpawnerRecipe>> SOURCE_SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register(SOURCE_SPAWNER_RECIPE_ID, SourceSpawnerRecipe.Serializer::new);

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return ForgeRegistries.RECIPE_TYPES.getKey(this).toString();
        }
    }
}

