package com.github.jarva.arsadditions.common.block;

import com.github.jarva.arsadditions.common.block.tile.MagelightLanternTile;
import com.hollingsworth.arsnouveau.common.block.ITickableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class MagelightLantern extends LanternBlock implements ITickableBlock {
    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);
    public MagelightLantern() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .forceSolidOn()
                .requiresCorrectToolForDrops()
                .strength(3.5F)
                .sound(SoundType.LANTERN)
                .lightLevel((b) -> b.getValue(LIGHT_LEVEL))
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagelightLanternTile(pos, state);
    }
}
