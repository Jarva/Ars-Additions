package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.item.StabilizedWarpIndex;
import com.github.jarva.arsadditions.item.WarpIndex;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void registerRegistries(IEventBus bus){
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item> LECTERN_REMOTE;
    public static final RegistryObject<Item> ADVANCED_LECTERN_REMOTE;

    static {
        LECTERN_REMOTE = ITEMS.register("warp_index", WarpIndex::new);
        ADVANCED_LECTERN_REMOTE = ITEMS.register("stabilized_warp_index", StabilizedWarpIndex::new);
    }
}
