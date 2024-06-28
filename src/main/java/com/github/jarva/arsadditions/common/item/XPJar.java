package com.github.jarva.arsadditions.common.item;

import com.hollingsworth.arsnouveau.common.items.VoidJar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class XPJar extends VoidJar {
    public XPJar() {
        super();
    }

    private int remaining = 0;

    @Override
    public void preConsume(Player player, ItemStack jar, ItemStack voided, int amount) {
        int xp = (amount + this.remaining) / 2;
        this.remaining = (amount + this.remaining) % 2;
        player.giveExperiencePoints(xp);
    }
}
