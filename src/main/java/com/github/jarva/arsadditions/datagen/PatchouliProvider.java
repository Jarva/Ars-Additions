package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.registry.ArsNouveauRegistry;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;

import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class PatchouliProvider extends com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider {

    public PatchouliProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        for (AbstractSpellPart glyph : ArsNouveauRegistry.GLYPHS) {
            addGlyphPage(glyph);
        }

        for (AbstractRitual ritual : ArsNouveauRegistry.RITUALS) {
            addRitualPage(ritual);
        }

        for (PatchouliPage patchouliPage : pages) {
            saveStable(cache, patchouliPage.build(), patchouliPage.path());
        }

    }

    @Override
    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage(Setup.root + ".page." + getRegistryName(item.asItem()).getPath()))
                .withPage(recipePage);
        PatchouliPage page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
        this.pages.add(page);
        return page;
    }

    public void addFamiliarPage(AbstractFamiliarHolder familiarHolder) {
        PatchouliBuilder builder = new PatchouliBuilder(FAMILIARS, "entity." + Setup.root + "." + familiarHolder.getRegistryName().getPath())
                .withIcon(Setup.root + ":" + familiarHolder.getRegistryName().getPath())
                .withTextPage(Setup.root + ".familiar_desc." + familiarHolder.getRegistryName().getPath())
                .withPage(new EntityPage(familiarHolder.getRegistryName().toString()));
        this.pages.add(new PatchouliPage(builder, getPath(FAMILIARS, familiarHolder.getRegistryName().getPath())));
    }

    public void addRitualPage(AbstractRitual ritual) {
        PatchouliBuilder builder = new PatchouliBuilder(RITUALS, "item." + Setup.root + "." + ritual.getRegistryName().getPath())
                .withIcon(ritual.getRegistryName().toString())
                .withTextPage(ritual.getDescriptionKey())
                .withPage(new CraftingPage(Setup.root + ":tablet_" + ritual.getRegistryName().getPath()));

        this.pages.add(new PatchouliPage(builder, getPath(RITUALS, ritual.getRegistryName().getPath())));
    }

    public void addEnchantmentPage(Enchantment enchantment) {
        PatchouliBuilder builder = new PatchouliBuilder(ENCHANTMENTS, enchantment.getDescriptionId())
                .withIcon(getRegistryName(Items.ENCHANTED_BOOK).toString())
                .withTextPage(Setup.root + ".enchantment_desc." + getRegistryName(enchantment).getPath());

        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
            builder.withPage(new EnchantingPage(Setup.root + ":" + getRegistryName(enchantment).getPath() + "_" + i));
        }
        this.pages.add(new PatchouliPage(builder, getPath(ENCHANTMENTS, getRegistryName(enchantment).getPath())));
    }

    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName(Setup.root + ".glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage(Setup.root + ".glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
    }

    @Override
    public Path getPath(ResourceLocation category, String fileName) {
        return this.output.resolve("assets/" + Setup.root + "/patchouli_books/example/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
    }

    ImbuementPage ImbuementPage(ItemLike item) {
        return new ImbuementPage(Setup.root + ":imbuement_" + getRegistryName(item.asItem()).getPath());
    }
}
