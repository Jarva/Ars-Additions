package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.networking.NetworkHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ArsAdditionsClient {
    public static KeyMapping openTerm;

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientModEvents {
        @SubscribeEvent
        public static void initKeybinds(RegisterKeyMappingsEvent evt) {
            openTerm = new KeyMapping("key.ars_additions.open_lectern", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, KeyMapping.CATEGORY_GAMEPLAY);
            evt.register(openTerm);
        }
    }

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class ClientForgeEvents {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent evt) {
            if (Minecraft.getInstance().player == null || evt.phase == TickEvent.Phase.START)
                return;

            if(openTerm.consumeClick()) {
                NetworkHandler.openTerminal();
            }
        }
    }

    public static void clientSetup() {
    }

}
