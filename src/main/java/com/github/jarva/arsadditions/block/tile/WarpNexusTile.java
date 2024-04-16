package com.github.jarva.arsadditions.block.tile;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WarpNexusTile extends BlockEntity {
    public WarpNexusTile(BlockPos pos, BlockState blockState) {
        super(AddonBlockRegistry.WARP_NEXUS_TILE.get(), pos, blockState);
    }
}
