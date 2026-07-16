package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.client.renderers.EnchantingWixieCauldronRenderer;
import com.github.jarva.arsadditions.client.renderers.entity.MagicCarpetRenderer;
import com.github.jarva.arsadditions.client.renderers.tile.WarpNexusRenderer;
import com.github.jarva.arsadditions.client.util.CompassUtil;
import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import com.github.jarva.arsadditions.common.item.data.HaversackData;
import com.github.jarva.arsadditions.common.util.FillUtil;
import com.github.jarva.arsadditions.setup.networking.OpenTerminalPacket;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonEntityRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.math.Axis;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            event.registerEntityRenderer(AddonEntityRegistry.MAGIC_CARPET_ENTITY.get(), MagicCarpetRenderer::new);
        }
    }

    @EventBusSubscriber(modid = ArsAdditions.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        private static final float MAX_PLAYER_PITCH_TILT = 35.0F;
        private static final float MAX_PLAYER_SIDE_TILT = 18.0F;
        private static final Set<Integer> CARPET_TILTED_PLAYERS = new HashSet<>();

        @SubscribeEvent
        public static void clientTick(ClientTickEvent.Post evt) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null)
                return;

            boolean ridingCarpet = minecraft.player.getVehicle() instanceof MagicCarpetEntity;
            if (ridingCarpet) {
                minecraft.player.setSprinting(false);
            }

            if(openTerm.consumeClick()) {
                OpenTerminalPacket.openTerminal();
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            if (!(event.getItemStack().getItem() instanceof PerkItem perkItem) || perkItem.perk == null) {
                return;
            }

            if (!isShiftDown()) {
                return;
            }

            List<Component> tooltip = event.getToolTip();
            boolean removedHoldShift = tooltip.removeIf(component ->
                    component.getContents() instanceof TranslatableContents translatableContents
                            && "tooltip.ars_nouveau.hold_shift".equals(translatableContents.getKey())
            );
            if (!removedHoldShift) {
                return;
            }

            tooltip.add(Component.translatable(perkItem.perk.getDescriptionKey()));
        }

        private static boolean isShiftDown() {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.options.keyShift.isDown();
        }

        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            if (!(event.getEntity().getVehicle() instanceof MagicCarpetEntity carpet)) {
                return;
            }

            float pitchTilt = -Mth.clamp(Mth.lerp(event.getPartialTick(), carpet.xRotO, carpet.getXRot()), -MAX_PLAYER_PITCH_TILT, MAX_PLAYER_PITCH_TILT);
            float sideTilt = Mth.clamp(carpet.getSideTilt(), -MAX_PLAYER_SIDE_TILT, MAX_PLAYER_SIDE_TILT);
            float bodyYaw = Mth.rotLerp(event.getPartialTick(), event.getEntity().yBodyRotO, event.getEntity().yBodyRot);
            float yawDegrees = 180.0F - bodyYaw;
            event.getPoseStack().pushPose();
            event.getPoseStack().mulPose(Axis.YP.rotationDegrees(yawDegrees));
            event.getPoseStack().mulPose(Axis.XP.rotationDegrees(pitchTilt));
            event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(sideTilt));
            event.getPoseStack().mulPose(Axis.YP.rotationDegrees(-yawDegrees));
            CARPET_TILTED_PLAYERS.add(event.getEntity().getId());
        }

        @SubscribeEvent
        public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
            if (CARPET_TILTED_PLAYERS.remove(event.getEntity().getId())) {
                event.getPoseStack().popPose();
            }
        }

    }

    public static void clientSetup() {

    }

}
