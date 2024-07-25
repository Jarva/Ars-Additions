package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.item.*;
import com.github.jarva.arsadditions.common.item.curios.Charm;
import com.github.jarva.arsadditions.common.item.curios.StabilizedWarpIndex;
import com.github.jarva.arsadditions.common.item.curios.WarpIndex;
import com.github.jarva.arsadditions.setup.registry.names.AddonItemNames;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonItemRegistry {
    public static final List<ItemRegistryWrapper<Item>> REGISTERED_ITEMS = new ArrayList<>();
    public static final List<ItemRegistryWrapper<Item>> DATAGEN_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    public static final ItemRegistryWrapper<Item> LECTERN_REMOTE;
    public static final ItemRegistryWrapper<Item> ADVANCED_LECTERN_REMOTE;
    public static final ItemRegistryWrapper<Item> CODEX_ENTRY;
    public static final ItemRegistryWrapper<Item> CODEX_ENTRY_LOST;
    public static final ItemRegistryWrapper<Item> CODEX_ENTRY_ANCIENT;
    public static final ItemRegistryWrapper<Item> UNSTABLE_RELIQUARY;
    public static final ItemRegistryWrapper<Item> EXPLORATION_WARP_SCROLL;
    public static final ItemRegistryWrapper<Item> NEXUS_WARP_SCROLL;
    public static final ItemRegistryWrapper<Item> XP_JAR;
    public static final ItemRegistryWrapper<Item> HANDY_HAVERSACK;
    public static final ItemRegistryWrapper<Item> ADVANCED_DOMINION_WAND;
    public static final ItemRegistryWrapper<Item> WAYFINDER;
    public static final ItemRegistryWrapper<Item> IMBUED_SPELL_PARCHMENT;

    public static final HashMap<CharmRegistry.CharmType, ItemRegistryWrapper<Item>> CHARMS = new HashMap<>();

    static {
        LECTERN_REMOTE = register(AddonItemNames.WARP_INDEX, WarpIndex::new);
        ADVANCED_LECTERN_REMOTE = register(AddonItemNames.STABILIZED_WARP_INDEX, StabilizedWarpIndex::new);
        CODEX_ENTRY = register(AddonItemNames.CODEX_ENTRY, CodexEntry::new);
        CODEX_ENTRY_LOST = register(AddonItemNames.CODEX_ENTRY_LOST, CodexEntryLost::new);
        CODEX_ENTRY_ANCIENT = register(AddonItemNames.CODEX_ENTRY_ANCIENT, CodexEntryAncient::new);
        UNSTABLE_RELIQUARY = register(AddonItemNames.UNSTABLE_RELIQUARY, UnstableReliquary::new);
        EXPLORATION_WARP_SCROLL = register(AddonItemNames.EXPLORATION_WARP_SCROLL, ExplorationWarpScroll::new);
        NEXUS_WARP_SCROLL = register(AddonItemNames.NEXUS_WARP_SCROLL, NexusWarpScroll::new);
        XP_JAR = register(AddonItemNames.XP_JAR, XPJar::new);
        HANDY_HAVERSACK = register(AddonItemNames.HANDY_HAVERSACK, HandyHaversack::new, false);
        ADVANCED_DOMINION_WAND = register(AddonItemNames.ADVANCED_DOMINION_WAND, AdvancedDominionWand::new);
        WAYFINDER = register(AddonItemNames.WAYFINDER, Wayfinder::new, false);
        IMBUED_SPELL_PARCHMENT = register(AddonItemNames.IMBUED_SPELL_PARCHMENT, ImbuedSpellParchment::new);

        registerCharms();
    }

    private static void registerCharms() {
        for (CharmRegistry.CharmType charm : CharmRegistry.CharmType.values()) {
            CHARMS.put(charm, register(charm.getSerializedName(), () -> new Charm(charm.getCharges())));
        }
    }

    private static ItemRegistryWrapper<Item> register(String name, Supplier<Item> item) {
        return register(name, item, true);
    }

    private static ItemRegistryWrapper<Item> register(String name, Supplier<Item> item, boolean dataGen) {
        ItemRegistryWrapper<Item> registered = new ItemRegistryWrapper<>(ITEMS.register(name, item));
        REGISTERED_ITEMS.add(registered);
        if (dataGen) DATAGEN_ITEMS.add(registered);
        return registered;
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}
