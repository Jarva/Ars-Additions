package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.item.NexusWarpScroll;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.SingleItemTile;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class WarpNexusTile extends SingleItemTile implements GeoBlockEntity, ITickable, IWololoable {
    private static final RawAnimation CLOSE = RawAnimation.begin().then("spin", Animation.LoopType.PLAY_ONCE).thenPlayAndHold("close");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("open").thenLoop("spin");
    private static final RawAnimation CLOSED = RawAnimation.begin().thenPlayAndHold("closed");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean hasNearbyPlayer = false;
    private boolean hasOpened = false;
    public AnimationController<WarpNexusTile> controller;
    private ParticleColor color = ParticleColor.defaultParticleColor();
    private ItemStackHandler inventory;

    public WarpNexusTile(BlockPos pos, BlockState blockState) {
        super(AddonBlockRegistry.WARP_NEXUS_TILE.get(), pos, blockState);
        int invSize = blockState.getValue(WarpNexus.HALF) == DoubleBlockHalf.LOWER ? 1 : 0;

        this.inventory = new ItemStackHandler(invSize) {
             @Override
             public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                 ItemStack scroll = super.extractItem(slot, amount, simulate);
                 if (!(scroll.getItem() instanceof NexusWarpScroll)) return scroll;

                 scroll.update(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true), (data) ->
                         data.setPos(pos.north(), level.dimension().location().toString()).setRotation(Vec2.ZERO)
                 );
                 return scroll;
             }

             @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                WarpNexusTile.this.setChanged();
            }
        };
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (getBlockState().getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;
        if (level.getGameTime() % 10 == 0) {
            Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
            hasNearbyPlayer = player != null;
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static final BlockCapability<IItemHandler, Void> ITEM_HANDLER =
            BlockCapability.createVoid(
                    ArsAdditions.prefix("warp_nexus_scroll"),
                    IItemHandler.class);

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.color = ParticleColorRegistry.from(tag.getCompound("color"));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("color", this.color.serialize());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controller = new AnimationController<>(this, this::predicate);
        controllerRegistrar.add(controller);
    }

    private <E extends BlockEntity & GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (event.getController().getCurrentAnimation() == null || (!hasNearbyPlayer && !hasOpened)) {
            return event.setAndContinue(CLOSED);
        }
        if (hasNearbyPlayer) {
            this.hasOpened = true;
            return event.setAndContinue(OPEN);
        }
        return event.setAndContinue(CLOSE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
        this.setChanged();
    }

    @Override
    public ParticleColor getColor() {
        return this.color;
    }

    public static Optional<WarpNexusTile> getWarpNexus(Level level, BlockPos pos) {
        Optional<WarpNexusTile> be = level.getBlockEntity(pos, AddonBlockRegistry.WARP_NEXUS_TILE.get());
        return be.flatMap(tile -> tile.getBlockState().getValue(WarpNexus.HALF) == DoubleBlockHalf.UPPER
                ? level.getBlockEntity(pos.below(), AddonBlockRegistry.WARP_NEXUS_TILE.get())
                : Optional.of(tile));
    }
}
