package com.github.jarva.arsadditions.common.item;

import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class XPJar extends VoidJar {
    public XPJar() {
        super();
    }

    @Override
    public void preConsume(Player player, ItemStack jar, ItemStack voided, int amount) {
        int remainder = jar.getOrDefault(AddonDataComponentRegistry.XP_JAR_REMAINDER, 0);
        int total = amount + remainder;
        player.giveExperiencePoints(total / 2);
        jar.set(AddonDataComponentRegistry.XP_JAR_REMAINDER, total % 2);
    }
}
