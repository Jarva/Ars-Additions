package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.mark.BrokenMarkData;
import com.github.jarva.arsadditions.common.item.data.mark.EmptyMarkData;
import com.github.jarva.arsadditions.common.item.data.mark.EntityMarkData;
import com.github.jarva.arsadditions.common.item.data.mark.LocationMarkData;
import com.github.jarva.arsadditions.common.item.data.mark.MarkData;
import com.github.jarva.arsadditions.common.util.codec.RegistryDispatcher;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MarkDataRegistry {
    public static final RegistryDispatcher<MarkData> MARK_DATA_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
            ArsAdditions.prefix("mark_data"),
            MarkData::codec,
            builder->{});

    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<EntityMarkData>> ENTITY = MARK_DATA_DISPATCHER.defreg().register("entity", () -> EntityMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<LocationMarkData>> LOCATION = MARK_DATA_DISPATCHER.defreg().register("location", () -> LocationMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<EmptyMarkData>> EMPTY = MARK_DATA_DISPATCHER.defreg().register("empty", () -> EmptyMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<BrokenMarkData>> BROKEN = MARK_DATA_DISPATCHER.defreg().register("broken", () -> BrokenMarkData.CODEC);

    public static void init() {

    }
}
