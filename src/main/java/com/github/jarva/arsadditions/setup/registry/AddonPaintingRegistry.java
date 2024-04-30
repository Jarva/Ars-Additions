package com.github.jarva.arsadditions.setup.registry;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonPaintingRegistry {
    public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registry.PAINTING_VARIANT.key(), MODID);
    public static final List<RegistryObject<PaintingVariant>> REGISTERED_PAINTINGS = new ArrayList<>();

    public static final RegistryObject<PaintingVariant> snoozebuncle = register("snoozebuncle", () -> new PaintingVariant(32, 32));

    public static RegistryObject<PaintingVariant> register(String name, Supplier<PaintingVariant> painting) {
        RegistryObject<PaintingVariant> variant = PAINTINGS.register(name, painting);
        REGISTERED_PAINTINGS.add(variant);
        return variant;
    }
}
