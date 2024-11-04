package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class AddonSetup {
    public static void registers(IEventBus modEventBus) {
        modEventBus.addListener(AddonSetup::registerRegistries);

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
        AddonConditionRegistry.CONDITION_CODECS.register(modEventBus);
        AddonCreativeTabRegistry.TABS.register(modEventBus);
        Triggers.TRIGGERS.register(modEventBus);
        MarkDataRegistry.MARK_DATA.register(modEventBus);
        ModifyTagRegistry.TAG_MODIFIER.register(modEventBus);

        DungeonLootTables.BASIC_LOOT.add(() -> new ItemStack(AddonItemRegistry.CODEX_ENTRY.get(), 1));
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(MarkDataRegistry.MARK_DATA_REGISTRY);
        event.register(ModifyTagRegistry.TAG_MODIFIER_REGISTRY);
    }
}
