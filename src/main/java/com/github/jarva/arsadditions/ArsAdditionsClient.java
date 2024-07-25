package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.client.renderers.EnchantingWixieCauldronRenderer;
import com.github.jarva.arsadditions.client.renderers.tile.WarpNexusRenderer;
import com.github.jarva.arsadditions.client.util.BookUtil;
import com.github.jarva.arsadditions.client.util.CompassUtil;
import com.github.jarva.arsadditions.common.util.FillUtil;
import com.github.jarva.arsadditions.mixin.PageTextAccessor;
import com.github.jarva.arsadditions.setup.networking.OpenTerminalPacket;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.page.PageText;

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
                    CompoundTag tag = stack.getTag();
                    if (tag == null) return 0.0F;
                    CompoundTag BET = tag.getCompound("BlockEntityTag");
                    return FillUtil.getFillLevel(BET.getInt("source"), BET.getInt("max_source"));
                });
                ItemProperties.register(AddonItemRegistry.HANDY_HAVERSACK.get(), ArsAdditions.prefix("loaded"), (stack, level, entity, seed) -> {
                    CompoundTag tag = stack.getTag();
                    if (tag == null) return 1.0F;
                    return tag.getBoolean("loaded") ? 0.0F : 1.0F;
                });
                ItemProperties.register(AddonItemRegistry.WAYFINDER.get(), ArsAdditions.prefix("angle"), new CompassItemPropertyFunction(new CompassUtil()));
                ItemProperties.register(AddonItemRegistry.WAYFINDER.get(), ArsAdditions.prefix("pos"), (stack, level, entity, seed) -> {
                    return stack.has(AddonDataComponentRegistry.WAYFINDER_DATA) ? 1.0F : 0.0F;
                });
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

        @SubscribeEvent
        public static void updateBookContents(BookContentsReloadEvent event) {
            ResourceLocation bookId = event.getBook();
            if (!bookId.equals(BookUtil.WORN_NOTEBOOK)) return;

            BookUtil.addRelation(
                    ArsNouveau.prefix("machines/storage_lectern"),
                    ArsNouveau.prefix("machines/warp_indexes")
            );
            BookPage wixiePage = BookUtil.newTextPage(
                    "ars_additions.page.wixie_enchanting_apparatus",
                    "ars_additions.page1.wixie_enchanting_apparatus"
            );
            BookUtil.addPage(ArsNouveau.prefix("automation/wixie_charm"), wixiePage, true, page -> {
                if (page instanceof PageText text) {
                    PageTextAccessor textAccessor = (PageTextAccessor) text;
                    String title = textAccessor.getTitle();
                    if (title == null) return false;
                    return title.equals("ars_nouveau.potion_crafting");
                }
                return false;
            });
            BookUtil.addPage(ArsNouveau.prefix("automation/wixie_charm"), wixiePage,
                    true, page -> BookUtil.isTextPage(page, "ars_nouveau.potion_crafting"));

            BookPage bulkScribing = BookUtil.newTextPage(
                    "ars_additions.page.bulk_scribing",
                    "ars_additions.page1.bulk_scribing"
            );
            BookUtil.addPage(ArsNouveau.prefix("machines/scribes_block"), bulkScribing,
                    true, page -> BookUtil.isTextPage(page, "ars_nouveau.scribing"));
        }
    }

    public static void clientSetup() {

    }

}
