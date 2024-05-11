package com.github.jarva.arsadditions.mixin;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WixieCauldronTile.class)
public interface WixieCauldronAccessor {
    @Accessor
    ItemStack getStackBeingCrafted();
}
