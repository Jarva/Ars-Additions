package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;
import static com.github.jarva.arsadditions.setup.registry.names.AddonItemNames.MAGIC_CARPET;

public class AddonEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MagicCarpetEntity>> MAGIC_CARPET_ENTITY = register(
            MAGIC_CARPET,
            EntityType.Builder.<MagicCarpetEntity>of(MagicCarpetEntity::new, MobCategory.MISC)
                    .sized(1.9F, 0.2F)
                    .setTrackingRange(10)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
    );

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(MODID + ":" + name));
    }
}
