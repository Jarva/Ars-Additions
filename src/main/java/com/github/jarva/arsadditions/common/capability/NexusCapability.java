package com.github.jarva.arsadditions.common.capability;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.networking.SyncNexusPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NexusCapability implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    private final Player player;

    public NexusCapability(Player player) {
        this.player = player;
    }
    public static final ResourceLocation IDENTIFIER = ArsAdditions.prefix("nexus");
    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            SyncNexusPacket.syncCapability(NexusCapability.this.player);
        }
    };
    private final LazyOptional<ItemStackHandler> optional = LazyOptional.of(() -> this.inventory);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("Inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag arg) {
        inventory.deserializeNBT(arg.getCompound("Inventory"));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return CapabilityRegistry.PLAYER_NEXUS_CAPABILITY.orEmpty(capability, this.optional);
    }
}
