package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.common.glyph.EffectMark;
import com.github.jarva.arsadditions.common.glyph.MethodRecall;
import com.github.jarva.arsadditions.common.glyph.MethodRetaliate;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
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

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class GlyphDatagen extends GlyphRecipeProvider {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public GlyphDatagen(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generatorIn);
        this.lookupProvider = lookupProvider;
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        lookupProvider.thenAccept(provider -> {
            addRecipe(MethodRetaliate.INSTANCE, i(Items.NETHERITE_SWORD), i(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(provider.holderOrThrow(Enchantments.THORNS), 3))));
        });
        addRecipe(MethodRecall.INSTANCE, i(ItemsRegistry.CONJURATION_ESSENCE), i(Items.ENDER_PEARL), i(ItemsRegistry.SCRYER_SCROLL), i(ItemsRegistry.SCRY_CASTER));
        addRecipe(EffectMark.INSTANCE, i(ItemsRegistry.MANIPULATION_ESSENCE), i(Items.ENDER_PEARL), i(BlockRegistry.MOB_JAR), i(RitualRegistry.getRitualItemMap().get(ArsNouveau.prefix(RitualLib.CONTAINMENT))));

        for (GlyphRecipe recipe : recipes) {
            Path path = getScribeGlyphPath(output, recipe.output.getItem());
            saveStable(cache, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), path);
        }
    }

    public Ingredient i(ItemLike item) {
        return Ingredient.of(item);
    }
    public Ingredient i(ItemStack item) {
        return Ingredient.of(item);
    }

    public void addRecipe(AbstractSpellPart part, Ingredient... items) {
        GlyphRecipe recipe = get(part);
        for (Ingredient item : items ) {
            recipe.withIngredient(item);
        }
        recipes.add(recipe);
    }

    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/" + Setup.root + "/recipes/glyph/" + getRegistryName(glyph).getPath() + ".json");
    }
}
