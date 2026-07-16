package com.github.jarva.arsadditions.common.perk;

import com.github.jarva.arsadditions.ArsAdditions;

public class CarpetSizePerk extends CarpetPerk {
    public static final CarpetSizePerk INSTANCE = new CarpetSizePerk();

    public CarpetSizePerk() {
        super(ArsAdditions.prefix("thread_carpet_size"));
    }

    @Override
    public String getLangName() {
        return "Carpet Size";
    }

    @Override
    public String getLangDescription() {
        return "Expands the Magic Carpet into a larger model with room for four riders.";
    }
}
