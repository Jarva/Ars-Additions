package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.glyph.MethodRetaliate;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;

import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class GlyphProvider extends GlyphRecipeProvider {
    public GlyphProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        addRecipe(MethodRetaliate.INSTANCE, i(Items.NETHERITE_SWORD), i(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.THORNS, 3))));

        for (GlyphRecipe recipe : recipes) {
            Path path = getScribeGlyphPath(output, recipe.output.getItem());
            saveStable(cache, recipe.asRecipe(), path);
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
