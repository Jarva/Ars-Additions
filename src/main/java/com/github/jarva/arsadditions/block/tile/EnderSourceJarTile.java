package com.github.jarva.arsadditions.block.tile;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.source.SourceJarEventQueue;
import com.github.jarva.arsadditions.storage.EnderSourceData;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class EnderSourceJarTile extends SourceJarTile implements ITickable {

    public static String OWNER_UUID_TAG = "owner_uuid";
    public EnderSourceJarTile(BlockPos pos, BlockState state) {
        super(AddonBlockRegistry.ENDER_SOURCE_JAR_TILE.get(), pos, state);
    }

    private UUID owner = null;
    private boolean registered = false;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        if (getLevel() != null && !getLevel().isClientSide) {
            int source = EnderSourceData.getSource(this.getLevel().getServer(), getOwner());
            this.setSource(source);
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide) return;

        if (level.getGameTime() % 120 == 0) {
            SourceJarEventQueue.addPosition(level, this.worldPosition);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (getOwner() != null) {
            tag.putUUID(OWNER_UUID_TAG, getOwner());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.hasUUID(OWNER_UUID_TAG)) {
            this.setOwner(tag.getUUID(OWNER_UUID_TAG));
        }
        super.load(tag);
    }

    @Override
    public int getSource() {
        if (getLevel() != null && !getLevel().isClientSide && getOwner() != null) {
            EnderSourceData.getSource(getLevel().getServer(), getOwner());
        }
        return super.getSource();
    }

    @Override
    public int setSource(int source) {
        if (getLevel() != null && !getLevel().isClientSide && getOwner() != null) {
            EnderSourceData.setSource(getLevel().getServer(), getOwner(), source);
        }
        return super.setSource(source);
    }

    @Override
    public boolean canAcceptSource() {
        if (getOwner() == null) return false;
        return super.canAcceptSource();
    }

    @Override
    public boolean canAcceptSource(int source) {
        if (getOwner() == null) return false;
        return super.canAcceptSource(source);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (getOwner() != null) {
            super.getTooltip(tooltip);
        }
    }
}
