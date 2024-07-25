package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.common.item.data.CharmData;
import com.github.jarva.arsadditions.common.item.data.HaversackData;
import com.github.jarva.arsadditions.common.item.data.WayfinderData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonDataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ArsAdditions.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HaversackData>> HAVERSACK_DATA = DATA.register("haversack_data",
            () -> DataComponentType.<HaversackData>builder().persistent(HaversackData.CODEC).networkSynchronized(HaversackData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CharmData>> CHARM_DATA = DATA.register("charm_data",
            () -> DataComponentType.<CharmData>builder().persistent(CharmData.CODEC).networkSynchronized(CharmData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AdvancedDominionData>> ADVANCED_DOMINION_DATA = DATA.register("advanced_dominion_data",
            () -> DataComponentType.<AdvancedDominionData>builder().persistent(AdvancedDominionData.CODEC).networkSynchronized(AdvancedDominionData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WayfinderData>> WAYFINDER_DATA = DATA.register("wayfinder_data",
            () -> DataComponentType.<WayfinderData>builder().persistent(WayfinderData.CODEC).networkSynchronized(WayfinderData.STREAM_CODEC).build()
    );
}
