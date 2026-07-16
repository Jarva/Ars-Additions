package com.github.jarva.arsadditions.common.perk;

import com.github.jarva.arsadditions.ArsAdditions;

public class CarpetSpeedPerk extends CarpetPerk {
    public static final CarpetSpeedPerk INSTANCE = new CarpetSpeedPerk();

    public CarpetSpeedPerk() {
        super(ArsAdditions.prefix("thread_carpet_speed"));
    }

    @Override
    public String getLangName() {
        return "Carpet Speed";
    }

    @Override
    public String getLangDescription() {
        return "Increases the Magic Carpet's maximum movement speed for each slot tier.";
    }
}
