package com.github.jarva.arsadditions.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    public static String root = MODID;

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new PatchouliProvider(gen));
        gen.addProvider(event.includeServer(), new LangDatagen(gen.getPackOutput(), root, "en_us"));
        gen.addProvider(event.includeServer(), new RecipeProvider(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new EnchantingAppProvider(gen));
    }

}