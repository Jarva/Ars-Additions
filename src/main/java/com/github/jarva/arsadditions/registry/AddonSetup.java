package com.github.jarva.arsadditions.registry;

import net.minecraftforge.eventbus.api.IEventBus;

public class AddonSetup {
    public static void registers(IEventBus modEventBus) {
        AddonBlockRegistry.BLOCKS.register(modEventBus);
        AddonBlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        AddonItemRegistry.ITEMS.register(modEventBus);
    }
}
