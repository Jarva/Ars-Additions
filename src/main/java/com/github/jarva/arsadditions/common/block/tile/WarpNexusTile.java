package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.item.NexusWarpScroll;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
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
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.util.GeckoLibUtil;

public class WarpNexusTile extends BlockEntity implements IAnimatable, ITickable {
    private static final AnimationBuilder CLOSE = new AnimationBuilder().playOnce("spin").playOnce("close").loop("closed");
    private static final AnimationBuilder OPEN = new AnimationBuilder().playOnce("open").loop("spin");
    private static final AnimationBuilder CLOSED = new AnimationBuilder().loop("closed");
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private boolean hasNearbyPlayer = false;
    private boolean hasOpened = false;
    public AnimationController<WarpNexusTile> controller;
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
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", this.inventory.serializeNBT());
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
    public void registerControllers(AnimationData animationData) {
        controller = new AnimationController<>(this, "Anim", 0, this::predicate);
        animationData.addAnimationController(controller);
    }

    private PlayState predicate(AnimationEvent<WarpNexusTile> event) {
        if (event.getController().getCurrentAnimation() == null || (!hasNearbyPlayer && !hasOpened)) {
            event.getController().setAnimation(CLOSED);
            return PlayState.CONTINUE;
        }
        if (hasNearbyPlayer) {
            this.hasOpened = true;
            event.getController().setAnimation(OPEN);
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(CLOSE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
