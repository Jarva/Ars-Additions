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

        this.add("block.ars_additions.ender_source_jar", "Ender Source Jar");

        this.add("item.ars_additions.warp_index", "Warp Index");
        this.add("item.ars_additions.stabilized_warp_index", "Stabilized Warp Index");
        this.add("item.ars_additions.codex_entry", "Codex Entry");
        this.add("item.ars_additions.lost_codex_entry", "Lost Codex Entry");
        this.add("item.ars_additions.ancient_codex_entry", "Ancient Codex Entry");

        this.add("item.ars_additions.unstable_reliquary", "Unstable Reliquary");
        this.add("tooltip.ars_additions.reliquary.marked.broken", "This Reliquary's tether is broken");
        this.add("tooltip.ars_additions.reliquary.marked.empty", "Bind this Reliquary to a target by holding it in your offhand and casting a spell with Mark");
        this.add("tooltip.ars_additions.reliquary.marked.entity", "Marked Entity:");
        this.add("tooltip.ars_additions.reliquary.marked.location", "Marked Location:");
        this.add("tooltip.ars_additions.reliquary.marked.location.pos", "X: %s, Y: %s, Z: %s");

        this.add("tooltip.ars_additions.warp_index.bound", "Bound to (%s, %s, %s) in %s");
        this.add("tooltip.ars_additions.warp_index.keybind", "Press the %s key to open");
        this.add("tooltip.ars_additions.warp_index.keybind.outline", "[%s]");

        this.add("key.ars_additions.open_lectern", "[Ars Additions] Activate Warp Index");

        this.add("chat.ars_additions.codex_entry.lore", "Teaches a random Tier %s glyph");
        this.add("chat.ars_additions.codex_entry.congratulations", "Congratulations!");
        this.add("chat.ars_additions.codex_entry.no_glyphs", "You've learned all this codex can teach you");

        this.add("chat.ars_additions.warp_index.bound", "Bound %s");
        this.add("chat.ars_additions.warp_index.unbound", "%s + %s to bind a %s");
        this.add("chat.ars_additions.warp_index.invalid_block", "Invalid %s");
        this.add("chat.ars_additions.warp_index.out_of_range", "Your %s is not loaded");
        this.add("chat.ars_additions.warp_index.no_activate", "You are unable to reach the %s here");
    }
}
