package com.github.jarva.arsadditions.common.block;

import com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile;
import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import com.github.jarva.arsadditions.common.util.FillUtil;
import com.hollingsworth.arsnouveau.common.block.ITickableBlock;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.TickableModBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnderSourceJar extends SourceJar implements ITickableBlock {
    public EnderSourceJar() {
        super(TickableModBlock.defaultProperties().noOcclusion(), "ender_source_jar");
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderSourceJarTile(pos, state);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState bs = super.getStateForPlacement(context);
        if (context.getPlayer() == null || context.getLevel().isClientSide) return bs;

        int source = EnderSourceData.getSource(context.getLevel().getServer(), context.getPlayer().getUUID());
        if (source == 0) return bs;

        int fill = FillUtil.getFillState(source);
        return bs.setValue(SourceJar.fill, fill);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer == null) return;
        if (level.getBlockEntity(pos) instanceof EnderSourceJarTile jar) {
            jar.setOwner(placer.getUUID());
        }
    }
}
