package com.github.jarva.arsadditions.common.block;

import com.github.jarva.arsadditions.common.block.tile.EnchantingWixieCauldronTile;
import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block; // Added import
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext; // Added import
import net.minecraft.world.phys.shapes.VoxelShape; // Added import
import net.minecraft.world.level.BlockGetter; // Added import

public class EnchantingWixieCauldron extends WixieCauldron {
    // Added VoxelShape field
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingWixieCauldronTile(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    // Added getShape method
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
