package com.github.jarva.arsadditions.common.block;

import com.github.jarva.arsadditions.common.block.tile.EnchantingWixieCauldronTile;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantingWixieCauldron extends WixieCauldron {
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingWixieCauldronTile(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
