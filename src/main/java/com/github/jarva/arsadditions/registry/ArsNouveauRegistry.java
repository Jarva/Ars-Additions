package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.glyph.MethodRetaliate;
import com.github.jarva.arsadditions.ritual.RitualChunkLoading;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> GLYPHS = new ArrayList<>();
    public static List<AbstractRitual> RITUALS = new ArrayList<>();
    public static void init() {
        registerRituals();
        registerGlyphs();
    }

    private static void registerGlyphs() {
        register(MethodRetaliate.INSTANCE);
    }

    private static void registerRituals() {
        register(new RitualChunkLoading());
    }

    private static void register(AbstractRitual ritual) {
        RitualRegistry.registerRitual(ritual);
        RITUALS.add(ritual);
    }

    private static void register(AbstractSpellPart glyph) {
        GlyphRegistry.registerSpell(glyph);
        GLYPHS.add(glyph);
    }
}
