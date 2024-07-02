package com.github.jarva.arsadditions.common.capability;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityRegistry {
    public static final Capability<ItemStackHandler> PLAYER_NEXUS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SuppressWarnings(value = "unused")
    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID)
    public static class CapabilityEventHandler {
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player player) {
                event.addCapability(NexusCapability.IDENTIFIER, new NexusCapability(player));
            }
        }
        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(NexusCapability.class);
        }
        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event) {
            Player old = event.getOriginal();
            old.reviveCaps();
            old.getCapability(PLAYER_NEXUS_CAPABILITY).ifPresent(oldCap -> event.getEntity().getCapability(PLAYER_NEXUS_CAPABILITY).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            }));
        }
    }
}
