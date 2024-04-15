package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.advancement.Triggers;
import com.github.jarva.arsadditions.config.CommonConfig;
import com.github.jarva.arsadditions.config.ServerConfig;
import com.github.jarva.arsadditions.networking.NetworkHandler;
import com.github.jarva.arsadditions.registry.AddonSetup;
import com.github.jarva.arsadditions.registry.ArsNouveauRegistry;
import com.hollingsworth.arsnouveau.setup.config.ANModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArsAdditions.MODID)
public class ArsAdditions {
    public static final String MODID = "ars_additions";
    public static final Logger LOGGER = LogManager.getLogger();

    public ArsAdditions() {
        ANModConfig commonConfig = new ANModConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_SPEC, ModLoadingContext.get().getActiveContainer(), MODID + "-common");
        ModLoadingContext.get().getActiveContainer().addConfig(commonConfig);
        ANModConfig serverConfig = new ANModConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_SPEC, ModLoadingContext.get().getActiveContainer(), MODID + "-server");
        ModLoadingContext.get().getActiveContainer().addConfig(serverConfig);

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        AddonSetup.registers(modbus);
        ArsNouveauRegistry.init();
        modbus.addListener(this::common);
        modbus.addListener(this::client);
        MinecraftForge.EVENT_BUS.register(this);

        Triggers.init();
    }

    public static ResourceLocation prefix(String path){
        return new ResourceLocation(MODID, path);
    }

    private void common(final FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    private void client(final FMLClientSetupEvent event) {
        ArsAdditionsClient.clientSetup();
    }
}
