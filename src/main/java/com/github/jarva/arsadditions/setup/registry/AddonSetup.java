package com.github.jarva.arsadditions.setup.registry;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;

public class AddonSetup {
    public static void registers(IEventBus modEventBus) {
//        CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);

        AddonBlockRegistry.BLOCKS.register(modEventBus);
        AddonBlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        AddonItemRegistry.ITEMS.register(modEventBus);
        AddonLootItemFunctionsRegistry.FUNCTION_TYPES.register(modEventBus);
        AddonEffectRegistry.EFFECTS.register(modEventBus);
        AddonPaintingRegistry.PAINTINGS.register(modEventBus);
        AddonRecipeRegistry.RECIPE_TYPES.register(modEventBus);
        AddonRecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        AddonDataComponentRegistry.DATA.register(modEventBus);
        AddonAttachmentRegistry.ATTACHMENT_TYPES.register(modEventBus);

        DungeonLootTables.BASIC_LOOT.add(() -> new ItemStack(AddonItemRegistry.CODEX_ENTRY.get(), 1));
    }
}
