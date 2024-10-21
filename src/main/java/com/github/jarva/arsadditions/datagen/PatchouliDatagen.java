package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.datagen.patchouli.ApparatusPageProvider;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.ArsNouveauRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.Comparator;
import java.util.Map;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class PatchouliDatagen extends com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider {

    public PatchouliDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    public static ResourceLocation STRUCTURES = ArsNouveau.prefix("structures");

    @Override
    public void collectJsons(CachedOutput cache) {
        for (AbstractSpellPart glyph : ArsNouveauRegistry.GLYPHS) {
            addGlyphPage(glyph);
        }

        for (AbstractRitual ritual : ArsNouveauRegistry.RITUALS) {
            addRitualPage(ritual);
        }

        addPage(new PatchouliBuilder(MACHINES, AddonItemRegistry.ADVANCED_LECTERN_REMOTE.get())
                        .withName("ars_additions.page.warp_indexes")
                        .withTextPage("ars_additions.page1.warp_indexes")
                        .withPage(new ApparatusPageProvider(AddonItemRegistry.LECTERN_REMOTE))
                        .withPage(new ApparatusPageProvider(AddonItemRegistry.ADVANCED_LECTERN_REMOTE))
                        .withPage(new RelationsPage().withEntry(MACHINES, "storage_lectern").withEntry(MACHINES, "bookwyrm_charm")),
                getPath(MACHINES, "warp_indexes")
        );

        addPage(new PatchouliBuilder(STRUCTURES, AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_LANTERN))
                    .withName("ars_additions.page.ruined_warp_portals")
                    .withTextPage("ars_additions.page1.ruined_warp_portals")
                        .withPage(new SpotlightPage(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get())),
                getPath(STRUCTURES, "ruined_warp_portals")
        );

        addPage(new PatchouliBuilder(MACHINES, AddonBlockRegistry.WARP_NEXUS.get())
                        .withTextPage("ars_additions.page1.warp_nexus")
                        .withTextPage("ars_additions.page2.warp_nexus")
                        .withPage(
                                new RelationsPage()
                                        .withEntry(STRUCTURES, "nexus_tower")
                        ),
                getPath(MACHINES, "warp_nexus")
        );

        addPage(new PatchouliBuilder(STRUCTURES, AddonBlockRegistry.WARP_NEXUS.get())
                        .withName("ars_additions.page.nexus_tower")
                        .withTextPage("ars_additions.page1.nexus_tower")
                        .withPage(new SpotlightPage(AddonBlockRegistry.WARP_NEXUS.get()).withText("ars_additions.spotlight.warp_nexus"))
                        .withPage(
                                new RelationsPage()
                                        .withEntry(MACHINES, "warp_nexus")
                        ),
                getPath(STRUCTURES, "nexus_tower")
        );

        addPage(new PatchouliBuilder(STRUCTURES, BlockRegistry.FLOURISHING_WOOD)
                        .withName("ars_nouveau.page.wilden_dens")
                        .withTextPage("ars_nouveau.page1.wilden_dens"),
                getPath(STRUCTURES, "wilden_dens")
        );

        addPage(new PatchouliBuilder(EQUIPMENT, AddonItemRegistry.UNSTABLE_RELIQUARY)
                        .withTextPage("ars_additions.page.unstable_reliquary")
                        .withPage(
                                new RelationsPage()
                                        .withEntry(GLYPHS_3, "glyph_mark")
                                        .withEntry(GLYPHS_3, "glyph_recall")
                        ),
                getPath(EQUIPMENT, "unstable_reliquary")
        );

        addBasicItem(AddonBlockRegistry.ENDER_SOURCE_JAR.get(), MACHINES, new ApparatusPageProvider(AddonBlockRegistry.ENDER_SOURCE_JAR));
        addBasicItem(AddonItemRegistry.XP_JAR.get(), EQUIPMENT, new ApparatusPageProvider(AddonItemRegistry.XP_JAR));
        addBasicItem(AddonItemRegistry.HANDY_HAVERSACK.get(), EQUIPMENT, new CraftingPage(AddonItemRegistry.HANDY_HAVERSACK));

        PatchouliBuilder charmBuilder = new PatchouliBuilder(EQUIPMENT, AddonItemRegistry.CHARMS.get(CharmRegistry.CharmType.FIRE_RESISTANCE))
                .withName("ars_additions.page.charms")
                .withTextPage("ars_additions.page1.charms");

        for (Map.Entry<CharmRegistry.CharmType, ItemRegistryWrapper<Item>> entry : AddonItemRegistry.CHARMS.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getName())).toList()) {
            CharmRegistry.CharmType charmType = entry.getKey();
            Item charm = entry.getValue().get();
            String name = "page.ars_additions." + charmType.getSerializedName() + ".title";
            String desc = "page.ars_additions." + charmType.getSerializedName() + ".desc";
            charmBuilder = charmBuilder
                    .withPage(
                            new SpotlightPage(charm)
                                    .withTitle(name)
                                    .withText(desc)
                                    .linkRecipe(true)
                    );
        }
        addPage(charmBuilder, getPath(EQUIPMENT, "charms"));

        addBasicItem(AddonItemRegistry.IMBUED_SPELL_PARCHMENT.get(), EQUIPMENT, null);

        addPage(new PatchouliBuilder(STRUCTURES, Blocks.BOOKSHELF)
                        .withName("ars_additions.page.arcane_library")
                        .withTextPage("ars_additions.page1.arcane_library"),
                getPath(STRUCTURES, "arcane_library")
        );

        for (PatchouliPage patchouliPage : pages) {
            saveStable(cache, patchouliPage.build(), patchouliPage.path());
        }
    }

    @Override
    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage(Setup.root + ".page." + getRegistryName(item.asItem()).getPath()));
        if (recipePage != null) {
            builder = builder.withPage(recipePage);
        }
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
}
