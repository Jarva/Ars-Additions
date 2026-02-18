package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.glyph.EffectMark;
import com.github.jarva.arsadditions.common.glyph.MethodRecall;
import com.github.jarva.arsadditions.datagen.Setup;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.GlyphEntry;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.registry.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry.*;

@EventBusSubscriber(modid = ArsAdditions.MODID)
public class AddonDocumentation {
    @SubscribeEvent
    public static void addPages(ReloadDocumentationEvent.AddEntries event) {
        for (AbstractSpellPart glyph : ArsNouveauRegistry.GLYPHS) {
            var entry = addPage(EntryBuilder.of(glyph)
                    .withName(Setup.root + ".glyph_name." + glyph.getRegistryName().getPath())
                    .withIcon(glyph.glyphItem)
                    .withPage(GlyphEntry.create(glyph))
                    .withCraftingPages(glyph.glyphItem));

            entry.withSearchTag(Component.translatable("ars_nouveau.keyword.glyph"));

            for (SpellSchool school : glyph.spellSchools) {
                entry.withSearchTag(school.getTextComponent());
                for (SpellSchool subschool : school.getSubSchools()) {
                    entry.withSearchTag(subschool.getTextComponent());
                }
            }
        }

        for (AbstractRitual ritual : ArsNouveauRegistry.RITUALS) {
            Item tablet = RitualRegistry.getRitualItemMap().get(ritual.getRegistryName());
            addPage(EntryBuilder.of(ritual)
                    .withName(ritual.getLangName())
                    .withIcon(tablet)
                    .withIntroPageNoIncrement(ritual.getDescriptionKey())
                    .withCraftingPages(Setup.root + ":tablet_" + ritual.getRegistryName().getPath(), tablet))
                    .withSearchTag(Component.translatable("ars_nouveau.keyword.ritual"));
        }

        DocCategory MACHINES = DocumentationRegistry.CRAFTING;
        DocCategory GETTING_STARTED = DocumentationRegistry.GETTING_STARTED;
        DocCategory SPELL_CASTING = DocumentationRegistry.SPELL_CASTING;
        DocCategory EQUIPMENT = DocumentationRegistry.ITEMS;
        DocCategory AUTOMATION = DocumentationRegistry.CRAFTING;
        DocCategory RESOURCES = DocumentationRegistry.FIELD_GUIDE;
        DocCategory STRUCTURES = DocumentationRegistry.FIELD_GUIDE;
        DocCategory SOURCE = DocumentationRegistry.SOURCE;
        DocCategory ARMOR = DocumentationRegistry.ARMOR;
        DocCategory RITUALS = DocumentationRegistry.RITUAL_INDEX;
        DocCategory ENCHANTMENTS = DocumentationRegistry.ENCHANTING;
        DocCategory FAMILIARS = DocumentationRegistry.FAMILIARS;
        DocCategory MOD_NEWS = DocumentationRegistry.GETTING_STARTED;

        addPage(EntryBuilder.of(MACHINES, AddonItemRegistry.ADVANCED_LECTERN_REMOTE)
                .withName("ars_additions.page.warp_indexes")
                .withIcon(AddonItemRegistry.ADVANCED_LECTERN_REMOTE)
                .withIntroPageNoIncrement("ars_additions.page1.warp_indexes")
                .withCraftingPages(AddonItemRegistry.LECTERN_REMOTE)
                .withCraftingPages(AddonItemRegistry.ADVANCED_LECTERN_REMOTE)
        ).withRelation(block(BlockRegistry.CRAFTING_LECTERN)).withRelation(item(ItemsRegistry.BOOKWYRM_CHARM));

        addPage(EntryBuilder.of(STRUCTURES, AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_LANTERN))
                .withName("ars_additions.page.ruined_warp_portals")
                .withIcon(AddonBlockRegistry.getBlock(AddonBlockNames.SOURCESTONE_LANTERN))
                .withIntroPageNoIncrement("ars_additions.page1.ruined_warp_portals")
                .withPage(TextEntry.create(Component.empty(), Component.translatable("item.ars_additions.exploration_warp_scroll"), AddonItemRegistry.EXPLORATION_WARP_SCROLL))
                .addConnectedSearch(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get())
        );

        var nexusTower = addPage(EntryBuilder.of(STRUCTURES, "nexus_tower")
                .withIcon(AddonBlockRegistry.WARP_NEXUS)
                .withIntroPageNoIncrement("ars_additions.page1.nexus_tower")
                .withPage(TextEntry.create(Component.translatable("ars_additions.spotlight.warp_nexus"), Component.translatable("block.ars_additions.warp_nexus"), AddonBlockRegistry.WARP_NEXUS))
        ).withRelation(BuiltInRegistries.BLOCK.getKey(AddonBlockRegistry.WARP_NEXUS.get()));

        addPage(EntryBuilder.of(MACHINES, AddonBlockRegistry.WARP_NEXUS)
                .withIcon(AddonBlockRegistry.WARP_NEXUS)
                .withIntroPageNoIncrement("ars_additions.page1.warp_nexus")
                .withTextPage("ars_additions.page2.warp_nexus")
        ).withRelation(nexusTower);

        addPage(EntryBuilder.of(STRUCTURES, BlockRegistry.FLOURISHING_WOOD)
                .withName("ars_nouveau.page.wilden_dens")
                .withIcon(BlockRegistry.FLOURISHING_WOOD)
                .withIntroPageNoIncrement("ars_nouveau.page1.wilden_dens")
        );

        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.UNSTABLE_RELIQUARY)
                .withIcon(AddonItemRegistry.UNSTABLE_RELIQUARY)
                .withIntroPageNoIncrement("ars_additions.page.unstable_reliquary")
        ).withRelation(glyph(EffectMark.INSTANCE)).withRelation(glyph(MethodRecall.INSTANCE));

        addPage(EntryBuilder.of(MACHINES, AddonBlockRegistry.ENDER_SOURCE_JAR).withIcon(AddonBlockRegistry.ENDER_SOURCE_JAR).withIntroPageNoIncrement("ars_additions.page.ender_source_jar").withCraftingPages(AddonBlockRegistry.ENDER_SOURCE_JAR));
        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.XP_JAR).withIcon(AddonItemRegistry.XP_JAR).withIntroPageNoIncrement("ars_additions.page.xp_jar").withCraftingPages(AddonItemRegistry.XP_JAR));
        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.HANDY_HAVERSACK).withIcon(AddonItemRegistry.HANDY_HAVERSACK).withIntroPageNoIncrement("ars_additions.page.handy_haversack").withCraftingPages(AddonItemRegistry.HANDY_HAVERSACK));

        DocEntryBuilder charmBuilder = EntryBuilder.of(EQUIPMENT, AddonItemRegistry.CHARMS.get(CharmRegistry.CharmType.FIRE_RESISTANCE))
                .withName("ars_additions.page.charms")
                .withIcon(AddonItemRegistry.CHARMS.get(CharmRegistry.CharmType.FIRE_RESISTANCE))
                .withIntroPageNoIncrement("ars_additions.page1.charms");

        for (Map.Entry<CharmRegistry.CharmType, ItemRegistryWrapper<Item>> entry : AddonItemRegistry.CHARMS.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getName())).toList()) {
            CharmRegistry.CharmType charmType = entry.getKey();
            Item charm = entry.getValue().get();
            String name = "page.ars_additions." + charmType.getSerializedName() + ".title";
            String desc = "page.ars_additions." + charmType.getSerializedName() + ".desc";
            charmBuilder = charmBuilder
                    .withPage(TextEntry.create(Component.translatable(desc), Component.translatable(name), charm))
                    .withCraftingPages(charm);
        }
        addPage(charmBuilder);

        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.IMBUED_SPELL_PARCHMENT).withIcon(AddonItemRegistry.IMBUED_SPELL_PARCHMENT).withIntroPageNoIncrement("ars_additions.page.imbued_spell_parchment").withCraftingPages(AddonItemRegistry.IMBUED_SPELL_PARCHMENT));

        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.MEMORY_CRYSTAL)
                .withIcon(AddonItemRegistry.MEMORY_CRYSTAL)
                .withIntroPageNoIncrement("ars_additions.page.memory_crystal")
                .withCraftingPages(AddonItemRegistry.MEMORY_CRYSTAL)
        );

        addPage(EntryBuilder.of(EQUIPMENT, AddonItemRegistry.ADVANCED_DOMINION_WAND)
                .withIcon(AddonItemRegistry.ADVANCED_DOMINION_WAND)
                .withIntroPageNoIncrement("ars_additions.page.advanced_dominion_wand")
                .withCraftingPages(AddonItemRegistry.ADVANCED_DOMINION_WAND)
        );

        addPage(EntryBuilder.of(MACHINES, AddonBlockRegistry.SOURCE_SPAWNER)
                .withIcon(AddonBlockRegistry.SOURCE_SPAWNER)
                .withIntroPageNoIncrement("ars_additions.page.source_spawner")
        );

        addPage(EntryBuilder.of(MACHINES, BlockRegistry.SCRIBES_BLOCK)
                .withName("ars_additions.page.bulk_scribing")
                .withIcon(BlockRegistry.SCRIBES_BLOCK)
                .withIntroPageNoIncrement("ars_additions.page1.bulk_scribing")
        );

        addPage(EntryBuilder.of(STRUCTURES, Blocks.BOOKSHELF)
                .withName("ars_additions.page.arcane_library")
                .withIcon(Blocks.BOOKSHELF)
                .withIntroPageNoIncrement("ars_additions.page1.arcane_library")
        );
    }

    @SubscribeEvent
    public static void editPages(ReloadDocumentationEvent.Post event) {
        DocEntry enchantingApparatus = block(BlockRegistry.ENCHANTING_APP_BLOCK);
        if (enchantingApparatus != null) {
            int insertIndex = Math.min(1, enchantingApparatus.pages().size());
            enchantingApparatus.pages().add(insertIndex, TextEntry.create(
                    Component.translatable("ars_additions.page1.wixie_enchanting_apparatus"),
                    Component.translatable("ars_additions.page.wixie_enchanting_apparatus")
            ));
        }

        DocEntry wixieCharm = item(ItemsRegistry.WIXIE_CHARM);
        if (wixieCharm != null) {
            // Insert after the "Multi-Item Crafting" page (index 4) in the Wixie Charm entry
            int insertIndex = Math.min(5, wixieCharm.pages().size());
            wixieCharm.pages().add(insertIndex, TextEntry.create(
                    Component.translatable("ars_additions.page1.wixie_enchanting_apparatus"),
                    Component.translatable("ars_additions.page.wixie_enchanting_apparatus")
            ));
        }
    }

    private static DocEntry block(BlockRegistryWrapper<? extends Block> block) {
        return getEntry(BuiltInRegistries.BLOCK.getKey(block.get()));
    }

    private static DocEntry item(ItemRegistryWrapper<? extends Item> item) {
        return getEntry(BuiltInRegistries.ITEM.getKey(item.get()));
    }

    private static DocEntry glyph(AbstractSpellPart item) {
        return getEntry(item.getRegistryName());
    }

    private static DocEntry addPage(DocEntryBuilder builder) {
        return DocumentationRegistry.registerEntry(builder.category, builder.build());
    }

    static class EntryBuilder extends DocEntryBuilder {
        public static EntryBuilder of(DocCategory category, String name) {
            return of(category, name, ArsAdditions.prefix(name));
        }

        public static EntryBuilder of(DocCategory category, String name, ResourceLocation entryId) {
            return new EntryBuilder(category, name.contains(".") ? name : Setup.root + ".page." + name, entryId);
        }

        public static EntryBuilder of(DocCategory category, ItemRegistryWrapper<? extends Item> item) {
            return of(category, item.get().getDescriptionId(), BuiltInRegistries.ITEM.getKey(item.get()));
        }

        public static EntryBuilder of(DocCategory category, ItemLike item) {
            return of(category, item.asItem().getDescriptionId(), BuiltInRegistries.ITEM.getKey(item.asItem()));
        }

        public static EntryBuilder of(AbstractSpellPart glyph) {
            return of(Documentation.glyphCategory(glyph.getConfigTier()), glyph.getRegistryName().getPath());
        }

        public static EntryBuilder of(AbstractRitual glyph) {
            return of(RITUAL_INDEX, glyph.getRegistryName().getPath());
        }

        public static EntryBuilder of(DocCategory category, BlockRegistryWrapper<? extends Block> block) {
            return of(category, block.get().getDescriptionId(), BuiltInRegistries.BLOCK.getKey(block.get()));
        }

        private EntryBuilder(DocCategory category, String name, ResourceLocation entryId) {
            super(category, name.contains(".") ? name : Setup.root + ".page." + name, entryId);
        }

        private EntryBuilder(DocCategory category, ItemLike itemLike) {
            super(category, itemLike);
        }
    }

}
