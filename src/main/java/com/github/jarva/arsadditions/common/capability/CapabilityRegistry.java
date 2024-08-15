package com.github.jarva.arsadditions.common.capability;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CapabilityRegistry {
    public static final EntityCapability<ItemStackHandler, Void> PLAYER_NEXUS = EntityCapability.createVoid(
            ArsAdditions.prefix("nexus"),
            ItemStackHandler.class
    );

    @EventBusSubscriber(modid = ArsAdditions.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CapabilityEventHandler {
        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    AddonBlockRegistry.WARP_NEXUS_TILE.get(),
                    (be, side) -> new ItemStackHandler(1)
            );
            event.registerEntity(
                    Capabilities.ItemHandler.ENTITY,
                    EntityType.PLAYER,
                    (player, context) -> new ItemStackHandler(9)
            );
        }
    }

    @SuppressWarnings(value = "unused")
    @EventBusSubscriber(modid = ArsAdditions.MODID)
    public static class CapabilityCloneEventHandler {
        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event) {
            RegistryAccess access = event.getEntity().registryAccess();
            event.getEntity().getCapability(PLAYER_NEXUS).deserializeNBT(access, event.getOriginal().getCapability(PLAYER_NEXUS).serializeNBT(access));
        }
    }
}
