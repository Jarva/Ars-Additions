package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.server.storage.ChunkLoadingData;
import com.github.jarva.arsadditions.common.util.DispenserExperienceGemBehavior;
import com.github.jarva.arsadditions.datagen.EnchantmentDatagen;
import com.github.jarva.arsadditions.server.util.AsyncLocator;
import com.github.jarva.arsadditions.setup.config.CommonConfig;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.setup.registry.AddonSetup;
import com.github.jarva.arsadditions.setup.registry.ArsNouveauRegistry;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArsAdditions.MODID)
public class ArsAdditions {
    public static final String MODID = "ars_additions";
    public static final Logger LOGGER = LogManager.getLogger();

    public ArsAdditions(IEventBus modEventBus, ModContainer modContainer) {
        AddonSetup.registers(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_SPEC);

        ArsNouveauRegistry.init();
        modEventBus.addListener(this::common);
        modEventBus.addListener(this::client);
        modEventBus.addListener(this::post);
        modEventBus.addListener(this::imc);
        modEventBus.addListener(this::registerTicketControllers);

        NeoForge.EVENT_BUS.addListener(this::setupExecutor);
        NeoForge.EVENT_BUS.addListener(this::shutdownExecutor);

        Triggers.init();
    }

    public static ResourceLocation prefix(String path){
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void common(final FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> {
            GenericRecipeRegistry.reloadAll(e.getServer().getRecipeManager());
        });
    }

    private void client(final FMLClientSetupEvent event) {
        ArsAdditionsClient.clientSetup();
    }

    private void post(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(ItemsRegistry.EXPERIENCE_GEM, new DispenserExperienceGemBehavior());
            DispenserBlock.registerBehavior(ItemsRegistry.GREATER_EXPERIENCE_GEM, new DispenserExperienceGemBehavior());
        });
    }

    private void imc(final InterModEnqueueEvent event) {
        InterModComms.sendTo(MODID, "apothic_enchanting", "set_ench_hard_cap",
                () -> Pair.of(EnchantmentDatagen.SPELLWEAVE_ENCHANTMENT, PerkSlot.PERK_SLOTS.values().stream().map(PerkSlot::value).max(Integer::compareTo).orElse(3))
        );
    }

    private void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(ChunkLoadingData.TICKET_CONTROLLER);
    }

    private void setupExecutor(ServerAboutToStartEvent event) {
        AsyncLocator.setupExecutorService();
    }

    private void shutdownExecutor(ServerStoppingEvent event) {
        AsyncLocator.shutdownExecutorService();
    }
}
