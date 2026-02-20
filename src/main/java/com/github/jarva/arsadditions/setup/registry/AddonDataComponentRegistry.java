package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.*;
import com.github.jarva.arsadditions.common.item.data.mark.MarkData;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;

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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MarkData>> MARK_DATA = DATA.register("mark_data",
            () -> DataComponentType.<MarkData>builder().persistent(MarkData.CODEC).networkSynchronized(MarkData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpBindData>> WARP_BIND_DATA = DATA.register("warp_bind_data",
            () -> DataComponentType.<WarpBindData>builder().persistent(WarpBindData.CODEC).networkSynchronized(WarpBindData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ExplorationScrollData>> EXPLORATION_SCROLL_DATA = DATA.register("exploration_scroll_data",
            () -> DataComponentType.<ExplorationScrollData>builder().persistent(ExplorationScrollData.CODEC).networkSynchronized(ExplorationScrollData.STREAM_CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> STRUCTURE_LOOKUP_DATA = DATA.register("structure_lookup_data",
            () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).build()
    );
    public static final DeferredHolder<DataComponentType<?> , DataComponentType<Boolean>> OVERRIDE_PERKS = DATA.register("override_perks",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> XP_JAR_REMAINDER = DATA.register("xp_jar_remainder",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MemoryCrystalData>> MEMORY_CRYSTAL_DATA = DATA.register("memory_crystal_data",
            () -> DataComponentType.<MemoryCrystalData>builder().persistent(MemoryCrystalData.CODEC).networkSynchronized(MemoryCrystalData.STREAM_CODEC).build()
    );
}
