package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.item.*;
import com.github.jarva.arsadditions.common.item.curios.Charm;
import com.github.jarva.arsadditions.common.item.curios.StabilizedWarpIndex;
import com.github.jarva.arsadditions.common.item.curios.WarpIndex;
import com.github.jarva.arsadditions.setup.registry.names.AddonItemNames;
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
    public static final List<RegistryObject<Item>> DATAGEN_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> LECTERN_REMOTE;
    public static final RegistryObject<Item> ADVANCED_LECTERN_REMOTE;
    public static final RegistryObject<Item> CODEX_ENTRY;
    public static final RegistryObject<Item> CODEX_ENTRY_LOST;
    public static final RegistryObject<Item> CODEX_ENTRY_ANCIENT;
    public static final RegistryObject<Item> UNSTABLE_RELIQUARY;
    public static final RegistryObject<Item> EXPLORATION_WARP_SCROLL;
    public static final RegistryObject<Item> NEXUS_WARP_SCROLL;
    public static final RegistryObject<Item> XP_JAR;
    public static final RegistryObject<Item> HANDY_HAVERSACK;
    public static final RegistryObject<Item> ADVANCED_DOMINION_WAND;
    public static final RegistryObject<Item> WAYFINDER;

    // Charms
    public static final RegistryObject<Item> FIRE_RESISTANCE_CHARM;
    public static final RegistryObject<Item> UNDYING_CHARM;
    public static final RegistryObject<Item> DISPEL_PROTECTION_CHARM;
    public static final RegistryObject<Item> FALL_PREVENTION_CHARM;
    public static final RegistryObject<Item> WATER_BREATHING_CHARM;
    public static final RegistryObject<Item> ENDER_MASK_CHARM;
    public static final RegistryObject<Item> VOID_PROTECTION_CHARM;

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

        FIRE_RESISTANCE_CHARM = register(AddonItemNames.FIRE_RESISTANCE_CHARM, () -> new Charm(1000));
        UNDYING_CHARM = register(AddonItemNames.UNDYING_CHARM, () -> new Charm(1));
        DISPEL_PROTECTION_CHARM = register(AddonItemNames.DISPEL_PROTECTION_CHARM, () -> new Charm(1));
        FALL_PREVENTION_CHARM = register(AddonItemNames.FALL_PREVENTION_CHARM, () -> new Charm(3));
        WATER_BREATHING_CHARM = register(AddonItemNames.WATER_BREATHING_CHARM, () -> new Charm(1000));
        ENDER_MASK_CHARM = register(AddonItemNames.ENDER_MASK_CHARM, () -> new Charm(100));
        VOID_PROTECTION_CHARM = register(AddonItemNames.VOID_PROTECTION_CHARM, () -> new Charm(3));
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> item) {
        return register(name, item, true);
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> item, boolean dataGen) {
        RegistryObject<Item> registered = ITEMS.register(name, item);
        REGISTERED_ITEMS.add(registered);
        if (dataGen) DATAGEN_ITEMS.add(registered);
        return registered;
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}
