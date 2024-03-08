package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.item.StabilizedWarpIndex;
import com.github.jarva.arsadditions.item.WarpIndex;
import com.github.jarva.arsadditions.registry.names.AddonItemNames;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> LECTERN_REMOTE;
    public static final RegistryObject<Item> ADVANCED_LECTERN_REMOTE;

    static {
        LECTERN_REMOTE = ITEMS.register(AddonItemNames.WARP_INDEX, WarpIndex::new);
        ADVANCED_LECTERN_REMOTE = ITEMS.register(AddonItemNames.STABILIZED_WARP_INDEX, StabilizedWarpIndex::new);
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}
