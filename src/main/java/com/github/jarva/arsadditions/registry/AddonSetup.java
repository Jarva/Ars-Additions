package com.github.jarva.arsadditions.registry;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

public class AddonSetup {
    public static void registers(IEventBus modEventBus) {
        AddonBlockRegistry.BLOCKS.register(modEventBus);
        AddonBlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        AddonItemRegistry.ITEMS.register(modEventBus);

        DungeonLootTables.BASIC_LOOT.add(() -> new ItemStack(AddonItemRegistry.CODEX_ENTRY.get(), 1 + DungeonLootTables.r.nextInt(2)));
    }
}
