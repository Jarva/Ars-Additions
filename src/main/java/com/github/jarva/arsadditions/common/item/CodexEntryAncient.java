package com.github.jarva.arsadditions.common.item;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.util.RandomSource;

public class CodexEntryAncient extends CodexEntry {
    @Override
    public int getExpAmount(RandomSource random) {
        return 160 * 3;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.THREE;
    }
}
