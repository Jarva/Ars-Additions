package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.item.*;
import com.github.jarva.arsadditions.registry.names.AddonItemNames;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonItemRegistry {
    public static final List<RegistryObject<Item>> REGISTERED_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> LECTERN_REMOTE;
    public static final RegistryObject<Item> ADVANCED_LECTERN_REMOTE;
    public static final RegistryObject<Item> CODEX_ENTRY;
    public static final RegistryObject<Item> CODEX_ENTRY_LOST;
    public static final RegistryObject<Item> CODEX_ENTRY_ANCIENT;

    static {
        LECTERN_REMOTE = register(AddonItemNames.WARP_INDEX, WarpIndex::new);
        ADVANCED_LECTERN_REMOTE = register(AddonItemNames.STABILIZED_WARP_INDEX, StabilizedWarpIndex::new);
        CODEX_ENTRY = register(AddonItemNames.CODEX_ENTRY, CodexEntry::new);
        CODEX_ENTRY_LOST = register(AddonItemNames.CODEX_ENTRY_LOST, CodexEntryLost::new);
        CODEX_ENTRY_ANCIENT = register(AddonItemNames.CODEX_ENTRY_ANCIENT, CodexEntryAncient::new);
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> item) {
        RegistryObject<Item> registered = ITEMS.register(name, item);
        REGISTERED_ITEMS.add(registered);
        return registered;
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}
