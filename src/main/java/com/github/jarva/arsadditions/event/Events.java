package com.github.jarva.arsadditions.event;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.registry.ModRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events {
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabRegistry.BLOCKS.getKey()) {
            event.accept(ModRegistry.LECTERN_REMOTE);
            event.accept(ModRegistry.ADVANCED_LECTERN_REMOTE);
        }
    }
}
