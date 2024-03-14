package com.github.jarva.arsadditions.event;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events {
    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabRegistry.BLOCKS.getKey()) {
            for (RegistryObject<Item> item : AddonItemRegistry.REGISTERED_ITEMS) {
                event.accept(item);
            }

            event.accept(AddonBlockRegistry.ENDER_SOURCE_JAR);
        }
    }
}
