package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.mixin.PageRelationsAccessor;
import com.github.jarva.arsadditions.networking.NetworkHandler;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.util.FillUtil;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.page.PageRelations;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArsAdditionsClient {
    public static KeyMapping openTerm;

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
                ItemProperties.register(AddonBlockRegistry.ENDER_SOURCE_JAR.get().asItem(), ArsAdditions.prefix( "source"), (stack, level, entity, seed) -> {
                    CompoundTag tag = stack.getTag();
                    if (tag == null) return 0.0F;
                    CompoundTag BET = tag.getCompound("BlockEntityTag");
                    return FillUtil.getFillLevel(BET.getInt("source"), BET.getInt("max_source"));
                });
            });
        }
        
//        @SubscribeEvent
//        public static void registerModels(ModelEvent.RegisterAdditional evt) {
//            evt.register(ReliquaryRenderer.MODEL_LOCATION);
//        }
    }

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent evt) {
            if (Minecraft.getInstance().player == null || evt.phase == TickEvent.Phase.START)
                return;

            if(openTerm.consumeClick()) {
                NetworkHandler.openTerminal();
            }
        }


        private static final ResourceLocation WORN_NOTEBOOK = new ResourceLocation(ArsNouveau.MODID, "worn_notebook");
        @SubscribeEvent
        public static void updateBookContents(BookContentsReloadEvent event) {
            ResourceLocation bookId = event.getBook();
            if (!bookId.equals(WORN_NOTEBOOK)) return;
            Book wornNotebook = BookRegistry.INSTANCE.books.get(WORN_NOTEBOOK);

            Map<ResourceLocation, BookEntry> entries = wornNotebook.getContents().entries;

            BookEntry storageLectern = entries.get(new ResourceLocation(ArsNouveau.MODID, "machines/storage_lectern"));
            if (storageLectern == null) return;
            List<BookPage> pages = storageLectern.getPages();
            Optional<BookPage> relationsPage = pages.stream().filter(page -> page instanceof PageRelations).findFirst();
            if (relationsPage.isEmpty()) return;

            PageRelationsAccessor relations = (PageRelationsAccessor) relationsPage.get();
            relations.getEntries().add(entries.get(new ResourceLocation(ArsNouveau.MODID, "machines/warp_indexes")));
        }
    }

    public static void clientSetup() {

    }

}
