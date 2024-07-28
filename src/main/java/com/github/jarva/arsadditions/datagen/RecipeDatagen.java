package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.github.jarva.arsadditions.common.ritual.RitualLocateStructure;
import com.github.jarva.arsadditions.datagen.conditions.ConfigCondition;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry.getBlock;

public class RecipeDatagen extends com.hollingsworth.arsnouveau.common.datagen.RecipeDatagen implements IConditionBuilder {
    public RecipeDatagen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        this.consumer = consumer;

        addChainRecipe(getBlock(AddonBlockNames.ARCHWOOD_CHAIN), BlockRegistry.ARCHWOOD_PLANK);
        addChainRecipe(getBlock(AddonBlockNames.GOLDEN_CHAIN), Items.GOLD_INGOT);
        addChainRecipe(getBlock(AddonBlockNames.SOURCESTONE_CHAIN), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), true);
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_CHAIN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_CHAIN), item -> item.withPrefix("crafting/chains/"));
        addStonecutter(getBlock(AddonBlockNames.SOURCESTONE_CHAIN), BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE));

        addMagelightLanternRecipe(getBlock(AddonBlockNames.ARCHWOOD_MAGELIGHT_LANTERN), BlockRegistry.ARCHWOOD_PLANK);
        addMagelightLanternRecipe(getBlock(AddonBlockNames.GOLDEN_MAGELIGHT_LANTERN), Items.GOLD_NUGGET);
        addMagelightLanternRecipe(getBlock(AddonBlockNames.SOURCESTONE_MAGELIGHT_LANTERN), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE));
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_MAGELIGHT_LANTERN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_MAGELIGHT_LANTERN), item -> item.withPrefix("crafting/lanterns/"));
        addMagelightLanternRecipe(getBlock(AddonBlockNames.MAGELIGHT_LANTERN), Items.IRON_INGOT);
        addMagelightLanternRecipe(getBlock(AddonBlockNames.SOUL_MAGELIGHT_LANTERN), Items.SOUL_SAND);

        addLanternRecipe(getBlock(AddonBlockNames.ARCHWOOD_LANTERN), BlockRegistry.ARCHWOOD_PLANK);
        addLanternRecipe(getBlock(AddonBlockNames.GOLDEN_LANTERN), Items.GOLD_NUGGET);
        addLanternRecipe(getBlock(AddonBlockNames.SOURCESTONE_LANTERN), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE));
        addBiDirectionalRecipe(getBlock(AddonBlockNames.SOURCESTONE_LANTERN), getBlock(AddonBlockNames.POLISHED_SOURCESTONE_LANTERN), item -> item.withPrefix("crafting/lanterns/"));

        addRitualRecipe(RitualChunkLoading.RESOURCE_LOCATION, builder ->
                builder.requires(BlockRegistry.CASCADING_LOG)
                    .requires(Items.NETHER_STAR)
                    .requires(ItemsRegistry.SOURCE_GEM)
                    .requires(ItemsRegistry.EARTH_ESSENCE),
                new ConfigCondition("ritual_enabled")
        );

        addButtonRecipe(getBlock(AddonBlockNames.SOURCESTONE_BUTTON), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), true);
        addButtonRecipe(getBlock(AddonBlockNames.POLISHED_SOURCESTONE_BUTTON), BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE), true);

        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_WALL), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), true);
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_SOURCESTONE_WALL), AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_SOURCESTONE), true);
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.POLISHED_SOURCESTONE_WALL), BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE), true);
        addWallRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_POLISHED_SOURCESTONE_WALL), AddonBlockRegistry.getBlock(AddonBlockNames.CRACKED_POLISHED_SOURCESTONE), true);

        Block sourcestone = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE);
        for (String name : AddonBlockNames.DECORATIVE_SOURCESTONES) {
            Block block = AddonBlockRegistry.getBlock(name);
            addStonecutter(sourcestone, block);
            shapelessBuilder(sourcestone).requires(block).save(consumer, getRecipeId(block, "crafting/revert/", true).withSuffix("_to_sourcestone"));
        }

        addDoorRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_DOOR), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), true);
        addDoorRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.POLISHED_SOURCESTONE_DOOR), BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE), true);

        addTrapdoorRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_TRAPDOOR), BlockRegistry.getBlock(LibBlockNames.SOURCESTONE), true);
        addTrapdoorRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.POLISHED_SOURCESTONE_TRAPDOOR), BlockRegistry.getBlock(LibBlockNames.SMOOTH_SOURCESTONE), true);

        addTrapdoorRecipe(AddonBlockRegistry.getBlock(AddonBlockNames.MAGEBLOOM_CARPET), BlockRegistry.getBlock(LibBlockNames.MAGEBLOOM_BLOCK));

        shapedBuilder(AddonItemRegistry.HANDY_HAVERSACK.get())
                .pattern("sgs")
                .pattern("mem")
                .pattern("mmm")
                .define('s', Items.STRING)
                .define('g', Items.GOLD_INGOT)
                .define('m', Items.PURPLE_WOOL)
                .define('e', Items.ENDER_PEARL)
                .save(consumer);

        shapedBuilder(AddonItemRegistry.WAYFINDER.get())
                .pattern(" g ")
                .pattern("gag")
                .pattern(" g ")
                .define('g', Items.GOLD_INGOT)
                .define('a', Items.AMETHYST_SHARD)
                .save(consumer);

        addRitualRecipe(RitualLocateStructure.RESOURCE_LOCATION, builder ->
                builder.requires(BlockRegistry.VEXING_LOG)
                    .requires(Items.COMPASS)
                    .requires(ItemTagProvider.SOURCE_GEM_TAG)
                    .requires(AddonItemRegistry.WAYFINDER.get())
        );

        shapelessBuilder(AddonItemRegistry.ADVANCED_DOMINION_WAND.get()).requires(AddonItemRegistry.ADVANCED_DOMINION_WAND.get()).save(consumer);
    }

    public void addRitualRecipe(ResourceLocation id, Function<ShapelessRecipeBuilder, ShapelessRecipeBuilder> modifier) {
        addRitualRecipe(id, modifier, null);
    }

    public void addRitualRecipe(ResourceLocation id, Function<ShapelessRecipeBuilder, ShapelessRecipeBuilder> modifier, ICondition condition) {
        ShapelessRecipeBuilder ritualBuilder = modifier.apply(shapelessBuilder(RitualRegistry.getRitualItemMap().get(id)));

        if (condition != null) {
            ConditionalRecipe.builder()
                    .addCondition(condition)
                    .addRecipe(ritualBuilder::save)
                    .generateAdvancement()
                    .build(consumer, id.withPrefix("ritual/"));
        } else {
            ritualBuilder.save(consumer, id.withPrefix("ritual/"));
        }

    }

    public void addButtonRecipe(ItemLike result, ItemLike material) {
        addButtonRecipe(result, material, false);
    }

    public void addButtonRecipe(ItemLike result, ItemLike material, boolean stonecutter) {
        shapelessBuilder(result).requires(material).save(consumer, getRecipeId(result, "crafting/buttons/"));

        if (stonecutter) {
            addStonecutter(result, material);
        }
    }

    public void addTrapdoorRecipe(ItemLike result, ItemLike material) {
        addTrapdoorRecipe(result, material, false);
    }

    public void addTrapdoorRecipe(ItemLike result, ItemLike material, boolean stonecutter) {
        shapedBuilder(result)
                .pattern("mm")
                .define('m', material)
                .save(consumer, getRecipeId(result, "crafting/trapdoors/"));
        if (stonecutter) {
            addStonecutter(result, material);
        }
    }

    public void addDoorRecipe(ItemLike result, ItemLike material) {
        addDoorRecipe(result, material, false);
    }

    public void addDoorRecipe(ItemLike result, ItemLike material, boolean stonecutter) {
        shapedBuilder(result)
                .pattern("mm")
                .pattern("mm")
                .pattern("mm")
                .define('m', material)
                .save(consumer, getRecipeId(result, "crafting/doors/"));
        if (stonecutter) {
            addStonecutter(result, material);
        }
    }

    public void addWallRecipe(ItemLike result, ItemLike material) {
        addWallRecipe(result, material, false);
    }

    public void addWallRecipe(ItemLike result, ItemLike material, boolean stonecutter) {
        shapedBuilder(result, 6)
                .pattern("   ")
                .pattern("mmm")
                .pattern("mmm")
                .define('m', material)
                .save(consumer, RecipeBuilder.getDefaultRecipeId(result).withPrefix("crafting/walls/"));
        if (stonecutter) {
            addStonecutter(result, material);
        }
    }

    public void addChainRecipe(ItemLike result, ItemLike material) {
        addChainRecipe(result, material, false);
    }

    public void addChainRecipe(ItemLike result, ItemLike material, boolean stonecutter) {
        shapedBuilder(result)
                .pattern("i")
                .pattern("m")
                .pattern("i")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .save(consumer, getRecipeId(result, "crafting/chains/"));
        if (stonecutter) {
            addStonecutter(result, material);
        }
    }

    public void addMagelightLanternRecipe(ItemLike result, ItemLike material) {
        addLanternRecipe(result, material, ItemsRegistry.SOURCE_GEM);
    }

    public void addLanternRecipe(ItemLike result, ItemLike material) {
        addLanternRecipe(result, material, Items.TORCH);
    }

    public void addLanternRecipe(ItemLike result, ItemLike material, ItemLike center) {
        shapedBuilder(result)
                .pattern("imi")
                .pattern("msm")
                .pattern("imi")
                .define('i', Items.IRON_NUGGET)
                .define('m', material)
                .define('s', center)
                .save(consumer, getRecipeId(result, "crafting/lanterns/"));
    }

    public void addBiDirectionalRecipe(ItemLike a, ItemLike b) {
        addBiDirectionalRecipe(a, b, (item) -> item);
    }

    public void addBiDirectionalRecipe(ItemLike a, ItemLike b, Function<ResourceLocation, ResourceLocation> applicator) {
        shapelessBuilder(a).requires(b).save(consumer, applicator.apply(getRecipeId(a, "reversed_")));
        shapelessBuilder(b).requires(a).save(consumer, applicator.apply(getRecipeId(b, "reversed_")));
    }

    private final HashMap<ResourceLocation, Integer> STONECUTTER_COUNTER = new HashMap<>();

    public void addStonecutter(ItemLike input, ItemLike output) {
        ResourceLocation location = getRecipeId(input, "crafting/stonecutter/", true);
        int counter = STONECUTTER_COUNTER.getOrDefault(location, 0);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.DECORATIONS, output).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemsRegistry.WORN_NOTEBOOK)).save(consumer, location.withSuffix("_" + counter));
        STONECUTTER_COUNTER.put(location, counter + 1);
    }

    public ResourceLocation getRecipeId(ItemLike input) {
        return getRecipeId(input, "", false);
    }

    public ResourceLocation getRecipeId(ItemLike input, String prefix) {
        return getRecipeId(input, prefix, false);
    }

    public ResourceLocation getRecipeId(ItemLike input, String prefix, boolean convert) {
        ResourceLocation loc = RecipeBuilder.getDefaultRecipeId(input).withPrefix(prefix);
        return convert? ArsAdditions.prefix(loc.getPath()) : loc;
    }
}
