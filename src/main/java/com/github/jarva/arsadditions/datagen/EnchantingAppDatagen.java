package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;

public class EnchantingAppDatagen extends ApparatusRecipeProvider {
    public EnchantingAppDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/apparatus/" + str + ".json");
    }

    @Override
    public void addEntries() {
        this.addRecipe(this.builder().withResult(AddonItemRegistry.LECTERN_REMOTE)
                .withReagent(ItemsRegistry.MUNDANE_BELT)
                .withPedestalItem(1, ItemsRegistry.SCRY_CASTER)
                .withPedestalItem(1, BlockRegistry.SCRYERS_CRYSTAL)
                .withPedestalItem(1, ItemsRegistry.STARBUNCLE_CHARM)
                .withPedestalItem(1, ItemsRegistry.BOOKWYRM_CHARM)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonItemRegistry.ADVANCED_LECTERN_REMOTE)
                .withReagent(AddonItemRegistry.LECTERN_REMOTE)
                .withPedestalItem(1, Items.NETHERITE_INGOT)
                .withPedestalItem(1, Items.NETHER_STAR)
                .withPedestalItem(1, Blocks.ENDER_CHEST)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonBlockRegistry.ENDER_SOURCE_JAR)
                .withReagent(BlockRegistry.SOURCE_JAR)
                .withPedestalItem(4, Items.ENDER_PEARL)
                .withPedestalItem(4, Items.POPPED_CHORUS_FRUIT)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonItemRegistry.UNSTABLE_RELIQUARY)
                .withReagent(BlockRegistry.MOB_JAR)
                .withPedestalItem(1, ItemsRegistry.CONJURATION_ESSENCE)
                .withPedestalItem(1, ItemsRegistry.MANIPULATION_ESSENCE)
                .withPedestalItem(1, Items.ENDER_PEARL)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonItemRegistry.XP_JAR)
                .withReagent(Items.GLASS_BOTTLE)
                .withPedestalItem(ItemsRegistry.ALLOW_ITEM_SCROLL)
                .withPedestalItem(Blocks.FURNACE)
                .withPedestalItem(Blocks.COBBLESTONE)
                .withPedestalItem(Ingredient.of(ItemTags.COALS))
                .withPedestalItem(Items.LAPIS_LAZULI)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonItemRegistry.ADVANCED_DOMINION_WAND)
                .withReagent(ItemsRegistry.DOMINION_ROD)
                .withPedestalItem(Items.AMETHYST_BLOCK)
                .withPedestalItem(Items.GOLD_INGOT)
                .withPedestalItem(Items.GOLD_INGOT)
                .build()
        );
        this.addRecipe(this.builder().withResult(AddonBlockRegistry.SOURCE_SPAWNER.get())
                .withReagent(ItemsRegistry.DRYGMY_CHARM)
                .withPedestalItem(ItemsRegistry.SUMMONING_FOCUS)
                .withPedestalItem(ItemsRegistry.CONJURATION_ESSENCE)
                .build()
        );

        for (CharmRegistry.CharmType charmType : CharmRegistry.CharmType.values()) {
            ApparatusRecipeBuilder builder = this.builder().withResult(AddonItemRegistry.CHARMS.get(charmType)).withReagent(Items.GLASS_BOTTLE);
            for (ItemLike item : charmType.getPedestalItems()) {
                builder.withPedestalItem(item);
            }
            this.addRecipe(builder.build());
        }
    }
}
