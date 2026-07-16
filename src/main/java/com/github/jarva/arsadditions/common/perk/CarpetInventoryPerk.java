package com.github.jarva.arsadditions.common.perk;

import com.github.jarva.arsadditions.ArsAdditions;

public class CarpetInventoryPerk extends CarpetPerk {
    public static final CarpetInventoryPerk INSTANCE = new CarpetInventoryPerk();

    public CarpetInventoryPerk() {
        super(ArsAdditions.prefix("thread_carpet_inventory"));
    }

    @Override
    public String getLangName() {
        return "Carpet Inventory";
    }

    @Override
    public String getLangDescription() {
        return "Adds owner-only storage to the Magic Carpet. Higher slots unlock more space.";
    }
}
