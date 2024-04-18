package com.github.jarva.arsadditions.datagen;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

public class LangDatagen extends LanguageProvider {

    public LangDatagen(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        for (AbstractSpellPart spell : GlyphRegistry.getSpellpartMap().values()) {
            ResourceLocation registry = spell.getRegistryName();
            if (!registry.getNamespace().equals(Setup.root)) continue;
            this.add(Setup.root + ".glyph_desc." + registry.getPath(), spell.getBookDescription());
            this.add(Setup.root + ".glyph_name." + registry.getPath(), spell.getName());
        }
        for (AbstractRitual ritual : RitualRegistry.getRitualMap().values()) {
            ResourceLocation registry = ritual.getRegistryName();
            if (!registry.getNamespace().equals(Setup.root)) continue;
            this.add("item." + Setup.root + "." + registry.getPath(), ritual.getLangName());
            this.add(ritual.getDescriptionKey(), ritual.getLangDescription());
        }

        // Blocks

        this.add("block.ars_additions.ender_source_jar", "Ender Source Jar");
        this.add("block.ars_additions.warp_nexus", "Warp Nexus (WIP)");
        this.add("block.ars_additions.archwood_chain", "Archwood Chain");
        this.add("block.ars_additions.golden_chain", "Golden Chain");
        this.add("block.ars_additions.sourcestone_chain", "Sourcestone Chain");
        this.add("block.ars_additions.polished_sourcestone_chain", "Polished Sourcestone Chain");
        this.add("block.ars_additions.archwood_lantern", "Archwood Lantern");
        this.add("block.ars_additions.golden_lantern", "Golden Lantern");
        this.add("block.ars_additions.sourcestone_lantern", "Sourcestone Lantern");
        this.add("block.ars_additions.polished_sourcestone_lantern", "Polished Sourcestone Lantern");
        this.add("block.ars_additions.archwood_magelight_lantern", "Archwood Magelight Lantern");
        this.add("block.ars_additions.golden_magelight_lantern", "Golden Magelight Lantern");
        this.add("block.ars_additions.sourcestone_magelight_lantern", "Sourcestone Magelight Lantern");
        this.add("block.ars_additions.polished_sourcestone_magelight_lantern", "Polished Sourcestone Magelight Lantern");
        this.add("block.ars_additions.magelight_lantern", "Magelight Lantern");
        this.add("block.ars_additions.soul_magelight_lantern", "Soul Magelight Lantern");
        this.add("block.ars_additions.sourcestone_wall", "Sourcestone Wall");
        this.add("block.ars_additions.polished_sourcestone_wall", "Polished Sourcestone Wall");
        this.add("block.ars_additions.cracked_sourcestone_wall", "Cracked Sourcestone Wall");
        this.add("block.ars_additions.cracked_polished_sourcestone_wall", "Cracked Polished Sourcestone Wall");
        this.add("block.ars_additions.sourcestone_button", "Sourcestone Button");
        this.add("block.ars_additions.polished_sourcestone_button", "Polished Sourcestone Button");
        this.add("block.ars_additions.cracked_sourcestone", "Cracked Sourcestone");
        this.add("block.ars_additions.cracked_polished_sourcestone", "Cracked Polished Sourcestone");
        this.add("block.ars_additions.cracked_sourcestone_large_bricks", "Cracked Sourcestone: Large Bricks");
        this.add("block.ars_additions.cracked_polished_sourcestone_large_bricks", "Cracked Polished Sourcestone: Large Bricks");
        this.add("block.ars_additions.cracked_sourcestone_small_bricks", "Cracked Sourcestone: Small Bricks");
        this.add("block.ars_additions.cracked_polished_sourcestone_small_bricks", "Cracked Polished Sourcestone: Small Bricks");

        // Items

        this.add("item.ars_additions.warp_index", "Warp Index");
        this.add("item.ars_additions.stabilized_warp_index", "Stabilized Warp Index");
        this.add("item.ars_additions.codex_entry", "Codex Entry");
        this.add("item.ars_additions.lost_codex_entry", "Lost Codex Entry");
        this.add("item.ars_additions.ancient_codex_entry", "Ancient Codex Entry");
        this.add("item.ars_additions.exploration_warp_scroll", "Explorer's Warp Scroll");
        this.add("item.ars_additions.unstable_reliquary", "Unstable Reliquary");

        // Tooltips

        this.add("tooltip.ars_additions.exploration_warp_scroll.use", "Throw into the frame of a ruined warp portal or press %s to teleport.");

        this.add("tooltip.ars_additions.reliquary.marked", "%s");
        this.add("tooltip.ars_additions.reliquary.marked.name", "%s \"%s\"");
        this.add("tooltip.ars_additions.reliquary.marked.location", "X: %s, Y: %s, Z: %s");
        this.add("tooltip.ars_additions.reliquary.marked.broken", "This Reliquary's tether is broken");
        this.add("tooltip.ars_additions.reliquary.marked.empty", "Bind this Reliquary to a target by holding it in your offhand and casting a spell with Mark");

        this.add("tooltip.ars_additions.warp_index.bound", "Bound to (%s, %s, %s) in %s");
        this.add("tooltip.ars_additions.warp_index.keybind", "Press the %s key to open");
        this.add("tooltip.ars_additions.warp_index.keybind.outline", "[%s]");

        // Keybinds

        this.add("key.ars_additions.open_lectern", "[Ars Additions] Activate Warp Index");

        // Chat Messages

        this.add("chat.ars_additions.codex_entry.lore", "Teaches a random Tier %s glyph");
        this.add("chat.ars_additions.codex_entry.congratulations", "Congratulations!");
        this.add("chat.ars_additions.codex_entry.no_glyphs", "You've learned all this codex can teach you");

        this.add("chat.ars_additions.warp_index.bound", "Bound %s");
        this.add("chat.ars_additions.warp_index.unbound", "%s + %s to bind a %s");
        this.add("chat.ars_additions.warp_index.invalid_block", "Invalid %s");
        this.add("chat.ars_additions.warp_index.out_of_range", "Your %s is not loaded");
        this.add("chat.ars_additions.warp_index.no_activate", "You are unable to reach the %s here");

        // Effects

        this.add("effect.ars_additions.marked", "Marked");

        // Advancements

        this.add("ars_additions.adv.title.find_ruined_portal", "Ancient Gateway");
        this.add("ars_additions.adv.desc.find_ruined_portal", "Find a ruined warp portal");

        this.add("ars_additions.adv.title.create_ruined_portal", "How does this still work?");
        this.add("ars_additions.adv.desc.create_ruined_portal", "Activate a ruined warp portal");

        // Patchouli

        this.add("ars_additions.page.warp_indexes", "Warp Indexes");
        this.add("ars_additions.page1.warp_indexes", "Warp Indexes are used to remotely access your Storage Lecterns. The Warp Index allows you to access your Storage Lectern anywhere within the same dimension. The Stabilized Warp Index does not have the same limitation and will work in any dimension. Both of these Warp Indexes require your Storage Lectern to be chunk-loaded.");

        this.add("ars_additions.category.structures", "Structures");
        this.add("ars_additions.category.structures.desc", "Structures that can be found around your world to sprinkle in a little extra magic.");

        this.add("ars_additions.page.ruined_warp_portals", "Ruined Warp Portals");
        this.add("ars_additions.page1.ruined_warp_portals", "Ruined Warp Portals can be found scattered around the world, displaying an Ancient Warp Portal that has degraded over time. Accompanying these portals is a chest containing an Explorer's Warp Scroll. This scroll remains heavily imbued with magic after all these years and can activate the repaired portal with no additional source required.");

        this.add("ars_nouveau.page.wilden_dens", "Wilden Dens");
        this.add("ars_nouveau.page1.wilden_dens", "Wilden Dens can be found in forests far away from civilization. These Wilden Dens house the fearsome Wilden which you will need to defeat in order to harness their magic.");

        this.add("ars_additions.page.unstable_reliquary", "Reliquaries are able to store references to entities and locations to be targeted using the Recall glyph. To store a reference in a Reliquary, hold it in your off-hand and cast a spell with the Mark glyph.");
        this.add("ars_additions.page.ender_source_jar", "The Ender Source Jar allows you to store source in an ender-connected Source Jar. Each jar you place will link to the same pool of source, allowing you to use the Source Jar from anywhere.");
    }
}
