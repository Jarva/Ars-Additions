package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SconceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MagelightLanternTile extends SconceTile {
    public MagelightLanternTile(BlockPos pos, BlockState state) {
        super(AddonBlockRegistry.MAGELIGHT_LANTERN_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide || !lit) return;

        BlockPos pos = getBlockPos();
        BlockState state = getLevel().getBlockState(pos);

        if (!state.hasProperty(LanternBlock.HANGING)) return;
        boolean hanging = state.getValue(LanternBlock.HANGING);

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double yOffset = hanging ? 0.2 : 0.1;

        ParticleColor nextColor = this.color.nextColor(level.random);
        for (int i = 0; i < 10; i++) {
            level.addParticle(
                    GlowParticleData.createData(nextColor),
                    centerX, pos.getY() + yOffset + ParticleUtil.inRange(-0.00, 0.1), centerZ,
                    0, ParticleUtil.inRange(0.0, 0.01f), 0
            );
        }
    }
}
