package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.ritual.RitualChunkLoading;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;

public class ArsNouveauRegistry {
    public static void init() {
        registerRituals();
    }

    private static void registerRituals() {
        register(new RitualChunkLoading());
    }

    private static void register(AbstractRitual ritual) {
        RitualRegistry.registerRitual(ritual);
    }
}
