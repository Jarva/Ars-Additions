package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.common.util.DispenserExperienceGemBehavior;
import com.github.jarva.arsadditions.setup.config.CommonConfig;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.setup.networking.NetworkHandler;
import com.github.jarva.arsadditions.setup.registry.AddonSetup;
import com.github.jarva.arsadditions.setup.registry.ArsNouveauRegistry;
import com.github.jarva.arsadditions.setup.registry.recipes.GenericRecipeRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
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
        NeoForge.EVENT_BUS.register(this);

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

    public void post(final FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(ItemsRegistry.EXPERIENCE_GEM, new DispenserExperienceGemBehavior());
            DispenserBlock.registerBehavior(ItemsRegistry.GREATER_EXPERIENCE_GEM, new DispenserExperienceGemBehavior());
        });
    }
}
