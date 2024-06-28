package com.github.jarva.arsadditions.common.item.curios;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StabilizedWarpIndex extends WarpIndex {
    public StabilizedWarpIndex() {
        super();
    }

    public boolean canActivate(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
        return true;
    }
}
