package com.github.jarva.arsadditions.datagen;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public class LangDatagen extends LanguageProvider {

    public LangDatagen(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for (Supplier<Glyph> supplier : GlyphRegistry.getGlyphItemMap().values()) {
            Glyph glyph = supplier.get();
            if (!glyph.spellPart.getRegistryName().getNamespace().equals(Setup.root)) continue;
            this.add(Setup.root + ".glyph_desc." + glyph.spellPart.getRegistryName().getPath(), glyph.spellPart.getBookDescription());
            this.add(Setup.root + ".glyph_name." + glyph.spellPart.getRegistryName().getPath(), glyph.spellPart.getName());
        }

        this.add("item.ars_additions.warp_index", "Warp Index");
        this.add("item.ars_additions.stabilized_warp_index", "Stabilized Warp Index");

        this.add("tooltip.ars_additions.warp_index.bound", "Bound to (%s, %s, %s) in %s");
        this.add("tooltip.ars_additions.warp_index.keybind", "Press the %s key to open");
        this.add("tooltip.ars_additions.warp_index.keybind.outline", "[%s]");

        this.add("key.ars_additions.open_lectern", "[Ars Additions] Activate Warp Index");

        this.add("chat.ars_additions.warp_index.bound", "Bound %s");
        this.add("chat.ars_additions.warp_index.unbound", "%s + %s to bind a %s");
        this.add("chat.ars_additions.warp_index.invalid_block", "Invalid %s");
        this.add("chat.ars_additions.warp_index.out_of_range", "Your %s is not loaded");
        this.add("chat.ars_additions.warp_index.no_activate", "You are unable to reach the %s here");
    }
}
