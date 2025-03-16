package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EnchantmentDatagen extends DatapackBuiltinEntriesProvider {
    public EnchantmentDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, entries(), Set.of(ArsAdditions.MODID));
    }

    public static ResourceKey<Enchantment> SPELLWEAVE_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, ArsAdditions.prefix("spellweave"));

    public static RegistrySetBuilder entries() {
        return new RegistrySetBuilder()
                .add(Registries.ENCHANTMENT, bootstrap -> {
                    bootstrap.register(
                            SPELLWEAVE_ENCHANTMENT,
                            new Enchantment(
                                    Component.translatable("enchantment.ars_additions.spellweave"),
                                    new Enchantment.EnchantmentDefinition(
                                            bootstrap.lookup(Registries.ITEM).getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                            Optional.of(HolderSet.empty()),
                                            30,
                                            3,
                                            new Enchantment.Cost(3, 1),
                                            new Enchantment.Cost(4, 2),
                                            2,
                                            List.of(EquipmentSlotGroup.ARMOR)
                                    ),
                                    HolderSet.empty(),
                                    DataComponentMap.builder()
                                            .build()
                            )
                    );
                });
    }
}

