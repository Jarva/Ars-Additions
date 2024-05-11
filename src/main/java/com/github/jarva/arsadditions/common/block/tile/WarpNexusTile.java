package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.item.NexusWarpScroll;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WarpNexusTile extends BlockEntity implements GeoBlockEntity, ITickable, IWololoable {
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
                 WarpScroll.WarpScrollData data = new StableWarpScroll.StableScrollData(scroll);
                 data.setData(pos.north(), level.dimension().location().toString(), Vec2.ZERO);
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

    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? optional.cast() : super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.optional.invalidate();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.color = ParticleColorRegistry.from(tag.getCompound("color"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", this.inventory.serializeNBT());
        tag.put("color", this.color.serialize());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getStack() {
        return this.inventory.getStackInSlot(0);
    }

    public ItemStack extract() {
        return this.inventory.extractItem(0, Item.MAX_STACK_SIZE, false);
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
}
