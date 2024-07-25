package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

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
        this.add("block.ars_additions.warp_nexus", "Warp Nexus");
        this.add("block.ars_additions.enchanting_wixie_cauldron", "Wixie's Enchanting Apparatus");
        this.add("block.ars_additions.source_spawner", "Source Spawner");
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
        this.add("block.ars_additions.sourcestone_door", "Sourcestone Door");
        this.add("block.ars_additions.polished_sourcestone_door", "Polished Sourcestone Door");
        this.add("block.ars_additions.sourcestone_trapdoor", "Sourcestone Trapdoor");
        this.add("block.ars_additions.polished_sourcestone_trapdoor", "Polished Sourcestone Trapdoor");
        this.add("block.ars_additions.magebloom_carpet", "Magebloom Carpet");

        // Items

        this.add("item.ars_additions.warp_index", "Warp Index");
        this.add("item.ars_additions.stabilized_warp_index", "Stabilized Warp Index");
        this.add("item.ars_additions.codex_entry", "Codex Entry");
        this.add("item.ars_additions.lost_codex_entry", "Lost Codex Entry");
        this.add("item.ars_additions.ancient_codex_entry", "Ancient Codex Entry");
        this.add("item.ars_additions.exploration_warp_scroll", "Explorer's Warp Scroll");
        this.add("item.ars_additions.nexus_warp_scroll", "Nexus Warp Scroll");
        this.add("item.ars_additions.unstable_reliquary", "Unstable Reliquary");
        this.add("item.ars_additions.xp_jar", "Jar of Miner's Wisdom");
        this.add("item.ars_additions.handy_haversack", "Handy Haversack");
        this.add("item.ars_additions.advanced_dominion_wand", "Advanced Dominion Wand");
        this.add("item.ars_additions.wayfinder", "Wayfinder");
        this.add("item.ars_additions.bound_wayfinder", "Bound Wayfinder");
        this.add("item.ars_additions.imbued_spell_parchment", "Imbued Spell Parchment");

        for (CharmRegistry.CharmType charm : CharmRegistry.CharmType.values()) {
            this.add("page.ars_additions." + charm.getSerializedName() + ".title", charm.getName());
            this.add("item.ars_additions." + charm.getSerializedName(), "Charm of " + charm.getName());
            this.add("tooltip.ars_additions." + charm.getSerializedName(), charm.getDescription());
        }

        // Paintings

        this.add("painting.ars_additions.snoozebuncle.title", "Snoozebuncle");
        this.add("painting.ars_additions.snoozebuncle.author", "Gootastic");

        // Tooltips

        this.add("tooltip.ars_additions.exploration_warp_scroll.use", "Throw into the frame of a ruined warp portal or press %s to teleport.");
        this.add("tooltip.ars_additions.exploration_warp_scroll.desc", "This ancient warp scroll is filled to the brim with unstable magic.");
        this.add("tooltip.ars_additions.exploration_warp_scroll.locating", "Locating Structure...");
        this.add("tooltip.ars_additions.exploration_warp_scroll.failed", "Unable to locate a nearby structure.");
        this.add("tooltip.ars_additions.exploration_warp_scroll.located", "Located Structure. Good luck.");

        this.add("tooltip.ars_additions.reliquary.marked", "%s");
        this.add("tooltip.ars_additions.reliquary.marked.name", "%s \"%s\"");
        this.add("tooltip.ars_additions.reliquary.marked.location", "X: %s, Y: %s, Z: %s");
        this.add("tooltip.ars_additions.reliquary.marked.broken", "This Reliquary's tether is broken");
        this.add("tooltip.ars_additions.reliquary.marked.empty", "Bind this Reliquary to a target by holding it in your offhand and casting a spell with Mark");

        this.add("tooltip.ars_additions.warp_index.bound", "Bound to (%s, %s, %s) in %s");
        this.add("tooltip.ars_additions.warp_index.keybind", "Press the %s key to open");
        this.add("tooltip.ars_additions.warp_index.keybind.outline", "[%s]");

        this.add("tooltip.ars_additions.source_spawner.disabled", "Disabled");

        this.add("tooltip.ars_additions.handy_haversack.container", "Container");
        this.add("tooltip.ars_additions.handy_haversack.instructions", "Click items into the Handy Haversack, or click the Handy Haversack onto items");

        this.add("tooltip.ars_additions.advanced_dominion_wand.mode", "Mode: %s");
        this.add("tooltip.ars_additions.advanced_dominion_wand.mode.first", "Locked First");
        this.add("tooltip.ars_additions.advanced_dominion_wand.mode.second", "Locked Second");

        this.add("tooltip.ars_additions.ritual_locate_structure.name", "Locator");

        this.add("tooltip.ars_additions.charm.charges", "Charges %s / %s");
        this.add("tooltip.ars_additions.charm.desc", "Rechargeable");

        this.add("tooltip.ars_additions.charm.charging", "Charging: %s");
        this.add("tooltip.ars_additions.charm.charging_progress", "Charging Progress: %s");

        this.add("tooltip.ars_additions.imbued_spell_parchment.scribing", "Scribing: %s");
        this.add("tooltip.ars_additions.imbued_spell_parchment.scribing_progress", "Scribing Progress: %s");

        this.add("tooltip.ars_additions.wayfinder.distance", "%s blocks away");

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

        this.add("chat.ars_additions.warp_nexus.no_scrolls", "You don't have any warp destinations available.");
        this.add("chat.ars_additions.warp_nexus.no_scrolls.instruction", "Press %s+%s to store a Warp Scroll.");

        this.add("chat.ars_additions.handy_haversack.invalid", "The Handy Haversack must be bound before scribing.");

        this.add("chat.ars_additions.charm.charging_started", "Charging %s. Add source nearby to increase charge speed.");

        this.add("chat.ars_additions.imbued_spell_parchment.scribing_started", "Scribing %s. Add source nearby to increase scribe speed.");

        this.add("chat.ars_additions.advanced_dominion_wand.mode", "Set mode: %s");

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

        this.add("ars_additions.page1.warp_nexus", "A Warp Nexus allows interdimensional travel to nine locations of your choice. Using the Warp Nexus while Sneaking will open up an inventory to store nine different Warp Scrolls. Using the Warp Nexus without Sneaking will open up a Warp menu to allow you to pick your destination.");
        this.add("ars_additions.page2.warp_nexus", "Warp Nexus inside Nexus Towers are situated on-top of Source Leylines so they don't require any source to operate. Once moved, a Warp Nexus requires 1,000 source per teleport.");

        this.add("ars_additions.page.nexus_tower", "Nexus Tower");
        this.add("ars_additions.page1.nexus_tower", "Nexus Towers are located on converging points of Source Leylines allowing them to harness the natural source in the world to allow easier transportation around the realm.");
        this.add("ars_additions.spotlight.warp_nexus", "These towers use this natural source via their Warp Nexus, an ancient waystone containing a Nexus Scroll bound to the towers location.");

        this.add("ars_additions.page.wixie_enchanting_apparatus", "Enchanting Apparatus");
        this.add("ars_additions.page1.wixie_enchanting_apparatus", "To create a Wixie Enchanting Apparatus, use a Wixie Charm on an Enchanting Apparatus while Sneaking. The Wixie Enchanting Apparatus works in the exact same way as the Item Crafting mentioned previously.");

        this.add("ars_additions.page.xp_jar", "A jar that can destroy items on pickup and grants a small amount of XP in return. To turn the jar on and off, use the jar while sneaking. To add or remove an item to be destroyed by the jar, use the jar with an item in the off hand, or use an item on the Scribes Table with the jar placed on it. The jar must be in your hotbar to function.");
        this.add("ars_additions.page.handy_haversack", "After binding the Handy Haversack to a container, you can right-click items onto the haversack to deposit them into that container from anywhere. When the container is unloaded the haversack will close and not accept any items. You can automate the depositing of items by adding items to its filters. Use the haversack with an item in your off hand, or scribe the haversack using a Scribe's table with the item you wish to add.");

        this.add("ars_additions.page.charms", "Charms");
        this.add("ars_additions.page1.charms", "Charms are a set of curios enchanted with strong protection magics. They range from allowing you to walk on Powdered Snow, to saving you from the Warden's powerful Sonic Boom. Each charm has a specific amount of charges available to protect you. Once the charges have been depleted, pop them into an Imbuement chamber to charge them back up.");

        this.add("page.ars_additions.fire_resistance_charm.desc", "This charm enables you to walk through fire and swim in lava.");
        this.add("page.ars_additions.undying_charm.desc", "This charm provides the gift of the Second Wind, allowing you to keep going upon receiving fatal damage.");
        this.add("page.ars_additions.dispel_protection_charm.desc", "This charm allows you to shrug off a Dispel, and keep all your powerful buffs active.");
        this.add("page.ars_additions.fall_prevention_charm.desc", "Upon falling further than 3 blocks, this charm will activate to grant you the blessing of feather fall.");
        this.add("page.ars_additions.water_breathing_charm.desc", "After your air bubbles have been depleted, this charm will allow you to keep breathing underwater.");
        this.add("page.ars_additions.ender_mask_charm.desc", "This charm protects you from the angering gaze of the Enderman.");
        this.add("page.ars_additions.void_protection_charm.desc", "Once activated this charm will activate its warp magics and teleport you back to the last safe space you were on.");
        this.add("page.ars_additions.sonic_boom_protection_charm.desc", "Crafted by an Ancient Mage, this charm protects you from the sonic rage of the Warden.");
        this.add("page.ars_additions.wither_protection_charm.desc", "Forged in the fires of the Nether by a Wither Skeleton mage in hopes of curing themself, this charm allows you to shrug off the the effects of Wither.");
        this.add("page.ars_additions.golden_charm.desc", "This charm of pure gold is used to peruse Bastions and interact with Piglin Brutes without drawing aggression.");
        this.add("page.ars_additions.night_vision_charm.desc", "This handy little charm imbues you with Night Vision when you're in a low-light environment.");
        this.add("page.ars_additions.powdered_snow_walk_charm.desc", "This charm keeps you afloat over powdered snow, allowing you to walk over it without fear.");

        this.add("ars_additions.page.bulk_scribing", "Bulk Scribing");
        this.add("ars_additions.page1.bulk_scribing", "It's also possible to scribe items in bulk by placing a Spell Book or scribed Spell Parchment on a pedestal next to an imbuement chamber and then placing the blank parchment or other item to be scribed inside the imbuement chamber.");

        this.add("ars_additions.page.imbued_spell_parchment", "You can imbue source into a spell parchment to allow you to cast that spell without consuming mana. To cast with an imbued spell parchment, you need to hold use to gather up all the source in the parchment ready to release. It takes about half a second to gather up 100 mana worth of source from the parchment, so bigger spells will take a longer time to cast.");

        this.add("ars_additions.page.arcane_library", "Arcane Library");
        this.add("ars_additions.page1.arcane_library", "Arcane Libraries are a rare structure found in Archwood Forests, they are the remnants of an ancient magic civilization long left behind. Find treasures to fulfil all your magical needs, but beware of the Wilden roaming the halls.");
    }
}
