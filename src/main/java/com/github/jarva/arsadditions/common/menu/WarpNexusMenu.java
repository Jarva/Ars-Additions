package com.github.jarva.arsadditions.common.menu;

import com.github.jarva.arsadditions.common.capability.CapabilityRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class WarpNexusMenu  extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public WarpNexusMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
        super(MenuType.GENERIC_3x3, containerId);

        this.access = access;

        LazyOptional<ItemStackHandler> capability = playerInv.player.getCapability(CapabilityRegistry.PLAYER_NEXUS_CAPABILITY);

        capability.ifPresent(handler -> {
            createWarpNexusInventory(handler);
            createPlayerInventory(playerInv);
            createPlayerHotbar(playerInv);
        });
    }

    private void createWarpNexusInventory(ItemStackHandler handler) {
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 3; ++column) {
                this.addSlot(new SlotItemHandler(handler, column + row * 3, 62 + column * 18, 17 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        boolean isWarpScroll = stack.is(ItemsRegistry.WARP_SCROLL.get()) || stack.is(ItemsRegistry.STABLE_WARP_SCROLL.get()) || stack.is(AddonItemRegistry.NEXUS_WARP_SCROLL.get());
                        if (!isWarpScroll) return false;

                        WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
                        return data.isValid();
                    }
                });
            }
        }
    }

    private void createPlayerInventory(Inventory playerInv) {
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInv, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        for(int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInv, column, 8 + column * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return itemStack;

        ItemStack item = slot.getItem();
        itemStack = item.copy();

        if (index < 9) {
            if (!this.moveItemStackTo(item, 9, 45, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(item, 0, 9, false)) {
            return ItemStack.EMPTY;
        }

        if (item.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (item.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, item);

        return itemStack;
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, AddonBlockRegistry.WARP_NEXUS.get());
    }
}
