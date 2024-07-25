package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registries.PAINTING_VARIANT, MODID);
    public static final List<DeferredHolder<PaintingVariant, PaintingVariant>> REGISTERED_PAINTINGS = new ArrayList<>();

    public static final DeferredHolder<PaintingVariant, PaintingVariant> snoozebuncle = register("snoozebuncle", () -> new PaintingVariant(32, 32, ArsAdditions.prefix("snoozebuncle")));

    public static DeferredHolder<PaintingVariant, PaintingVariant> register(String name, Supplier<PaintingVariant> painting) {
        DeferredHolder<PaintingVariant, PaintingVariant> variant = PAINTINGS.register(name, painting);
        REGISTERED_PAINTINGS.add(variant);
        return variant;
    }
}
