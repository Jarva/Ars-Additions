package com.github.jarva.arsadditions;

import com.github.jarva.arsadditions.networking.NetworkHandler;
import com.github.jarva.arsadditions.registry.AddonSetup;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArsAdditions.MODID)
public class ArsAdditions {
    public static final String MODID = "ars_additions";
    public static final Logger LOGGER = LogManager.getLogger();

    public static KeyMapping openLectern;

    public ArsAdditions() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        AddonSetup.registers(modbus);
        modbus.addListener(this::common);
        modbus.addListener(this::client);
        MinecraftForge.EVENT_BUS.register(this);
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
