package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.mark.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MarkDataRegistry {
    public static final ResourceKey<Registry<MapCodec<? extends MarkData>>> MARK_DATA_REGISTRY_KEY = ResourceKey.createRegistryKey(ArsAdditions.prefix("mark_data"));
    public static final Registry<MapCodec<? extends MarkData>> MARK_DATA_REGISTRY = new RegistryBuilder<>(MARK_DATA_REGISTRY_KEY).create();
    public static final DeferredRegister<MapCodec<? extends MarkData>> MARK_DATA = DeferredRegister.create(MARK_DATA_REGISTRY, ArsAdditions.MODID);

    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<EntityMarkData>> ENTITY = MARK_DATA.register("entity", () -> EntityMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<LocationMarkData>> LOCATION = MARK_DATA.register("location", () -> LocationMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<EmptyMarkData>> EMPTY = MARK_DATA.register("empty", () -> EmptyMarkData.CODEC);
    private static final DeferredHolder<MapCodec<? extends MarkData>, MapCodec<BrokenMarkData>> BROKEN = MARK_DATA.register("broken", () -> BrokenMarkData.CODEC);
}
