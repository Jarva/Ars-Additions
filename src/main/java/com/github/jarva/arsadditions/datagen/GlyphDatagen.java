package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.common.glyph.EffectMark;
import com.github.jarva.arsadditions.common.glyph.MethodRecall;
import com.github.jarva.arsadditions.common.glyph.MethodRetaliate;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;

import java.io.IOException;
import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class GlyphDatagen extends GlyphRecipeProvider {
    public GlyphDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        addRecipe(MethodRetaliate.INSTANCE, i(Items.NETHERITE_SWORD), i(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.THORNS, 3))));
        addRecipe(MethodRecall.INSTANCE, i(ItemsRegistry.CONJURATION_ESSENCE), i(Items.ENDER_PEARL), i(ItemsRegistry.SCRYER_SCROLL), i(ItemsRegistry.SCRY_CASTER));
        addRecipe(EffectMark.INSTANCE, i(ItemsRegistry.MANIPULATION_ESSENCE), i(Items.ENDER_PEARL), i(BlockRegistry.MOB_JAR), i(ArsNouveauAPI.getInstance().getRitualItemMap().get(new ResourceLocation(ArsNouveau.MODID, RitualLib.CONTAINMENT))));

        for (GlyphRecipe recipe : recipes) {
            Path path = getScribeGlyphPath(generator.getOutputFolder(), recipe.output.getItem());
            DataProvider.saveStable(cache, recipe.asRecipe(), path);
        }
    }

    public Ingredient i(ItemLike item) {
        return Ingredient.of(item);
    }
    public Ingredient i(ItemStack item) {
        return StrictNBTIngredient.of(item);
    }

    public void addRecipe(AbstractSpellPart part, Ingredient... items) {
        GlyphRecipe recipe = get(part);
        for (Ingredient item : items ) {
            recipe.withIngredient(item);
        }
        recipes.add(recipe);
    }

    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/" + getRegistryName(glyph).getPath() + ".json");
    }
}
