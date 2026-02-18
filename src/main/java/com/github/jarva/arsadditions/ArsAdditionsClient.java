package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.client.renderers.EnchantingWixieCauldronRenderer;
import com.github.jarva.arsadditions.client.renderers.tile.WarpNexusRenderer;
import com.github.jarva.arsadditions.client.util.CompassUtil;
import com.github.jarva.arsadditions.common.item.data.HaversackData;
import com.github.jarva.arsadditions.common.util.FillUtil;
import com.github.jarva.arsadditions.setup.networking.OpenTerminalPacket;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
public class ArsAdditionsClient {
    public static KeyMapping openTerm;

    @EventBusSubscriber(modid = ArsAdditions.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void initKeybinds(RegisterKeyMappingsEvent evt) {
            openTerm = new KeyMapping("key.ars_additions.open_lectern", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, "key.category.ars_nouveau.general");
            evt.register(openTerm);
        }

        @SubscribeEvent
        public static void init(FMLClientSetupEvent evt) {
            ArsAdditions.LOGGER.info("Running init");
            evt.enqueueWork(() -> {
                ItemProperties.register(AddonBlockRegistry.ENDER_SOURCE_JAR.get().asItem(), ArsAdditions.prefix("source"), (stack, level, entity, seed) -> {
                    if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) return 0.0F;
                    int source = BlockFillContents.get(stack);
                    return FillUtil.getFillLevel(source);
                });
                ItemProperties.register(AddonItemRegistry.HANDY_HAVERSACK.get(), ArsAdditions.prefix("loaded"), (stack, level, entity, seed) -> {
                    HaversackData data = stack.get(AddonDataComponentRegistry.HAVERSACK_DATA);
                    return data != null && data.loaded() ? 0.0F : 1.0F;
                });
                ItemProperties.register(AddonItemRegistry.WAYFINDER.get(), ArsAdditions.prefix("angle"), new CompassItemPropertyFunction(new CompassUtil()));
                ItemProperties.register(AddonItemRegistry.WAYFINDER.get(), ArsAdditions.prefix("pos"), (stack, level, entity, seed) -> stack.has(AddonDataComponentRegistry.WAYFINDER_DATA) ? 1.0F : 0.0F);
            });
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(AddonBlockRegistry.WARP_NEXUS_TILE.get(), WarpNexusRenderer::new);
            event.registerBlockEntityRenderer(AddonBlockRegistry.WIXIE_ENCHANTING_TILE.get(), EnchantingWixieCauldronRenderer::new);
        }
    }

    @EventBusSubscriber(modid = ArsAdditions.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post evt) {
            if (Minecraft.getInstance().player == null)
                return;

            if(openTerm.consumeClick()) {
                OpenTerminalPacket.openTerminal();
            }
        }

    }

    public static void clientSetup() {

    }

}
