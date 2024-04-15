package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.datagen.conditions.ConfigCondition;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.datagen.RecipeDatagen;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class RecipeProvider extends RecipeDatagen implements IConditionBuilder {
    public RecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        this.consumer = consumer;

        addChainRecipe(AddonBlockRegistry.ARCHWOOD_CHAIN, i(BlockRegistry.ARCHWOOD_PLANK));
        addChainRecipe(AddonBlockRegistry.GOLDEN_CHAIN, i(Items.GOLD_INGOT));
        addChainRecipe(AddonBlockRegistry.SOURCESTONE_CHAIN, i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(AddonBlockRegistry.SOURCESTONE_CHAIN, AddonBlockRegistry.POLISHED_SOURCESTONE_CHAIN);

        addMagelightLanternRecipe(AddonBlockRegistry.ARCHWOOD_MAGELIGHT_LANTERN, i(BlockRegistry.ARCHWOOD_PLANK));
        addMagelightLanternRecipe(AddonBlockRegistry.GOLDEN_MAGELIGHT_LANTERN, i(Items.GOLD_NUGGET));
        addMagelightLanternRecipe(AddonBlockRegistry.SOURCESTONE_MAGELIGHT_LANTERN, i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(AddonBlockRegistry.SOURCESTONE_MAGELIGHT_LANTERN, AddonBlockRegistry.POLISHED_SOURCESTONE_MAGELIGHT_LANTERN);
        addMagelightLanternRecipe(AddonBlockRegistry.MAGELIGHT_LANTERN, i(Items.IRON_NUGGET));
        addMagelightLanternRecipe(AddonBlockRegistry.SOUL_MAGELIGHT_LANTERN, i(Items.SOUL_SAND));

        addLanternRecipe(AddonBlockRegistry.ARCHWOOD_LANTERN, i(BlockRegistry.ARCHWOOD_PLANK));
        addLanternRecipe(AddonBlockRegistry.GOLDEN_LANTERN, i(Items.GOLD_NUGGET));
        addLanternRecipe(AddonBlockRegistry.SOURCESTONE_LANTERN, i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(AddonBlockRegistry.SOURCESTONE_LANTERN, AddonBlockRegistry.POLISHED_SOURCESTONE_LANTERN);

        ShapelessRecipeBuilder chunkLoadingRitual = shapelessBuilder(RitualRegistry.getRitualItemMap().get(ArsAdditions.prefix("ritual_chunk_loading")))
                .requires(BlockRegistry.CASCADING_LOG)
                .requires(Items.NETHER_STAR)
                .requires(ItemsRegistry.SOURCE_GEM)
                .requires(ItemsRegistry.EARTH_ESSENCE);

        ConditionalRecipe.builder()
                .addCondition(new ConfigCondition("ritual_enabled"))
                .addRecipe(chunkLoadingRitual::save)
                .generateAdvancement()
                .build(consumer, ArsAdditions.prefix("ritual_chunk_loading"));
    }

    public void addChainRecipe(RegistryObject<? extends ItemLike> result, Ingredient material) {
        shapedBuilder(result.get())
                .pattern("i")
                .pattern("m")
                .pattern("i")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .save(consumer);
    }

    public void addMagelightLanternRecipe(RegistryObject<? extends ItemLike> result, Ingredient material) {
        addLanternRecipe(result, material, i(ItemsRegistry.SOURCE_GEM));
    }

    public void addLanternRecipe(RegistryObject<? extends ItemLike> result, Ingredient material) {
        addLanternRecipe(result, material, i(Items.TORCH));
    }

    public void addLanternRecipe(RegistryObject<? extends ItemLike> result, Ingredient material, Ingredient center) {
        shapedBuilder(result.get())
                .pattern("imi")
                .pattern("msm")
                .pattern("imi")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .define('s', center)
                .save(consumer);
    }

    public void addBiDirectionalRecipe(RegistryObject<? extends ItemLike> a, RegistryObject<? extends ItemLike> b) {
        addBiDirectionalRecipe(a.get(), b.get());
    }

    public void addBiDirectionalRecipe(ItemLike a, ItemLike b) {
        shapelessBuilder(a).requires(b).save(consumer, RecipeBuilder.getDefaultRecipeId(a).withPrefix("reversed_"));
        shapelessBuilder(b).requires(a).save(consumer, RecipeBuilder.getDefaultRecipeId(b).withPrefix("reversed_"));
    }

    public Ingredient i(ItemLike item) {
        return Ingredient.of(item);
    }
    public Ingredient i(ItemStack item) {
        return StrictNBTIngredient.of(item);
    }
}
