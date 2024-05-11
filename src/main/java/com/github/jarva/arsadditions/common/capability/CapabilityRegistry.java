package com.github.jarva.arsadditions.common.capability;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.networking.SyncNexusPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityRegistry {
    public static final Capability<ItemStackHandler> PLAYER_NEXUS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static LazyOptional<ItemStackHandler> getNexusScrolls(final Player player) {
        if (player == null) return LazyOptional.empty();
        return player.getCapability(PLAYER_NEXUS_CAPABILITY);
    }

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
        public static void playerClone(PlayerEvent.Clone event) {
            if (!event.isWasDeath()) return;
            Player old = event.getOriginal();
            old.reviveCaps();
            getNexusScrolls(old).ifPresent(oldNexus -> getNexusScrolls(event.getEntity()).ifPresent(newNexus -> {
                newNexus.deserializeNBT(oldNexus.serializeNBT());
            }));

            old.invalidateCaps();
        }

        @SubscribeEvent
        public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                SyncNexusPacket.syncCapability(player);
            }
        }


    }
}
