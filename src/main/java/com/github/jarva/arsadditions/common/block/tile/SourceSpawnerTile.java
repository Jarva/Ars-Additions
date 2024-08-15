package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.common.util.SourceSpawner;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SourceSpawnerTile extends BlockEntity implements ITickable, ITooltipProvider {
    public final SourceSpawner spawner = new SourceSpawner(this);

    public SourceSpawnerTile(BlockPos pos, BlockState blockState) {
        super(AddonBlockRegistry.SOURCE_SPAWNER_TILE.get(), pos, blockState);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (spawner.disabled) {
            tooltip.add(Component.translatable("tooltip.ars_additions.source_spawner.disabled").withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.spawner.load(this.level, this.worldPosition, tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        this.spawner.save(tag);
    }

    public int getDelaySignal() {
        double percentage = (double) spawner.spawnDelay / spawner.maxSpawnDelay;
        return (int) Math.ceil(percentage * 15);
    }

    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundtag = this.saveWithoutMetadata(provider);
        compoundtag.remove("SpawnPotentials");
        return compoundtag;
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
        handleUpdateTag(pkt.getTag() == null ? new CompoundTag() : pkt.getTag(), provider);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            this.spawner.serverTick(serverLevel, pos);
        } else {
            this.spawner.clientTick(level, pos);
        }
    }

    public BaseSpawner getSpawner() {
        return this.spawner;
    }
}
