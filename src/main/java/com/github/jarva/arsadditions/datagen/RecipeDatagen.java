package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.datagen.conditions.ConfigCondition;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
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
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

import static com.github.jarva.arsadditions.registry.AddonBlockRegistry.getBlock;

public class RecipeDatagen extends com.hollingsworth.arsnouveau.common.datagen.RecipeDatagen implements IConditionBuilder {
    public RecipeDatagen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        this.consumer = consumer;

        addChainRecipe(getBlock(AddonBlockNames.ARCHWOOD_CHAIN), i(BlockRegistry.ARCHWOOD_PLANK));
        addChainRecipe(getBlock(AddonBlockNames.GOLDEN_CHAIN), i(Items.GOLD_INGOT));
        addChainRecipe(getBlock(AddonBlockNames.SOURCESTONE_CHAIN), i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_CHAIN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_CHAIN));

        addMagelightLanternRecipe(getBlock(AddonBlockNames.ARCHWOOD_MAGELIGHT_LANTERN), i(BlockRegistry.ARCHWOOD_PLANK));
        addMagelightLanternRecipe(getBlock(AddonBlockNames.GOLDEN_MAGELIGHT_LANTERN), i(Items.GOLD_NUGGET));
        addMagelightLanternRecipe(getBlock(AddonBlockNames.SOURCESTONE_MAGELIGHT_LANTERN), i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_MAGELIGHT_LANTERN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_MAGELIGHT_LANTERN));
        addMagelightLanternRecipe(getBlock(AddonBlockNames.MAGELIGHT_LANTERN), i(Items.IRON_NUGGET));
        addMagelightLanternRecipe(getBlock(AddonBlockNames.SOUL_MAGELIGHT_LANTERN), i(Items.SOUL_SAND));

        addLanternRecipe(getBlock(AddonBlockNames.ARCHWOOD_LANTERN), i(BlockRegistry.ARCHWOOD_PLANK));
        addLanternRecipe(getBlock(AddonBlockNames.GOLDEN_LANTERN), i(Items.GOLD_NUGGET));
        addLanternRecipe(getBlock(AddonBlockNames.SOURCESTONE_LANTERN), i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_LANTERN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_LANTERN));

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

        shapelessBuilder(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_BUTTON)).requires(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)).save(consumer);
        shapelessBuilder(AddonBlockRegistry.getBlock(AddonBlockNames.POLISHED_SOURCESTONE_BUTTON)).requires(BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE)).save(consumer);

        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_WALL), i(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE)));
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_SOURCESTONE_WALL), i(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_SOURCESTONE)));
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.POLISHED_SOURCESTONE_WALL), i(BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE)));
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_POLISHED_SOURCESTONE_WALL), i(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_POLISHED_SOURCESTONE)));

        Block sourcestone = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        for (String name : AddonBlockNames.DECORATIVE_SOURCESTONES) {
            Block block = AddonBlockRegistry.getBlock(name);
            makeStonecutter(consumer, sourcestone, block, LibBlockNames.SOURCESTONE);
            shapelessBuilder(sourcestone).requires(block).save(consumer, ArsAdditions.prefix(name + "_to_sourcestone"));
        }
    }

    public void addWallRecipe(ItemLike result, Ingredient material) {
        shapedBuilder(result)
                .pattern("   ")
                .pattern("mmm")
                .pattern("mmm")
                .define('m', material)
                .save(consumer);
    }

    public void addChainRecipe(ItemLike result, Ingredient material) {
        shapedBuilder(result)
                .pattern("i")
                .pattern("m")
                .pattern("i")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .save(consumer);
    }

    public void addMagelightLanternRecipe(ItemLike result, Ingredient material) {
        addLanternRecipe(result, material, i(ItemsRegistry.SOURCE_GEM));
    }

    public void addLanternRecipe(ItemLike result, Ingredient material) {
        addLanternRecipe(result, material, i(Items.TORCH));
    }

    public void addLanternRecipe(ItemLike result, Ingredient material, Ingredient center) {
        shapedBuilder(result)
                .pattern("imi")
                .pattern("msm")
                .pattern("imi")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .define('s', center)
                .save(consumer);
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
