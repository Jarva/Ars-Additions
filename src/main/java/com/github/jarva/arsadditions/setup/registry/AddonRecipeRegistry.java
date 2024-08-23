package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.common.recipe.imbuement.BulkScribingRecipe;
import com.github.jarva.arsadditions.common.recipe.imbuement.CharmChargingRecipe;
import com.github.jarva.arsadditions.common.recipe.imbuement.ImbueSpellScrollRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonRecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final String SOURCE_SPAWNER_RECIPE_ID = "source_spawner";
    public static final String LOCATE_STRUCTURE_RECIPE_ID = "locate_structure";
    public static final String CHARM_CHARGING_RECIPE_ID = "charm_charging";
    public static final String BULK_SCRIBING_RECIPE_ID = "bulk_scribing";
    public static final String IMBUE_SCROLL_RECIPE_ID = "imbue_scroll";

    public static final DeferredHolder<RecipeType<?>, RecipeType<SourceSpawnerRecipe>> SOURCE_SPAWNER_TYPE = RECIPE_TYPES.register(SOURCE_SPAWNER_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, SourceSpawnerRecipe.Serializer> SOURCE_SPAWNER_SERIALIZER = RECIPE_SERIALIZERS.register(SOURCE_SPAWNER_RECIPE_ID, SourceSpawnerRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<LocateStructureRecipe>> LOCATE_STRUCTURE_TYPE = RECIPE_TYPES.register(LOCATE_STRUCTURE_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, LocateStructureRecipe.Serializer> LOCATE_STRUCTURE_SERIALIZER = RECIPE_SERIALIZERS.register(LOCATE_STRUCTURE_RECIPE_ID, LocateStructureRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CharmChargingRecipe>> CHARM_CHARGING_TYPE = RECIPE_TYPES.register(CHARM_CHARGING_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, CharmChargingRecipe.Serializer> CHARM_CHARGING_SERIALIZER = RECIPE_SERIALIZERS.register(CHARM_CHARGING_RECIPE_ID, CharmChargingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BulkScribingRecipe>> BULK_SCRIBING_TYPE = RECIPE_TYPES.register(BULK_SCRIBING_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, BulkScribingRecipe.Serializer> BULK_SCRIBING_SERIALIZER = RECIPE_SERIALIZERS.register(BULK_SCRIBING_RECIPE_ID, BulkScribingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ImbueSpellScrollRecipe>> IMBUE_SCROLL_TYPE = RECIPE_TYPES.register(IMBUE_SCROLL_RECIPE_ID, ModRecipeType::new);
    public static final DeferredHolder<RecipeSerializer<?>, ImbueSpellScrollRecipe.Serializer> IMBUE_SCROLL_SERIALIZER = RECIPE_SERIALIZERS.register(IMBUE_SCROLL_RECIPE_ID, ImbueSpellScrollRecipe.Serializer::new);

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        @Override
        public String toString() {
            return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
        }
    }
}

