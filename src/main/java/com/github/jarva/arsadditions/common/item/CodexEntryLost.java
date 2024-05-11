package com.github.jarva.arsadditions.common.item;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.util.RandomSource;

public class CodexEntryLost extends CodexEntry {
    @Override
    public int getExpAmount(RandomSource random) {
        return 160;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }
}
