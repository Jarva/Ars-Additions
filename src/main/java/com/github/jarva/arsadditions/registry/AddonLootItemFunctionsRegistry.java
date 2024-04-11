package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.loot.functions.ExplorationScrollFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonLootItemFunctionsRegistry {
    public static final DeferredRegister<LootItemFunctionType> FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);

    public static final RegistryObject<LootItemFunctionType> EXPLORATION_SCROLL_TYPE;

    static {
        EXPLORATION_SCROLL_TYPE = register("exploration_scroll", ExplorationScrollFunction.Serializer::new);
    }

    private static RegistryObject<LootItemFunctionType> register(String id, Supplier<Serializer<? extends LootItemFunction>> supplier) {
        return FUNCTION_TYPES.register(id, () -> new LootItemFunctionType(supplier.get()));
    }
}
