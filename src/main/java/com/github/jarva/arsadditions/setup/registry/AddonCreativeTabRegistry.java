package com.github.jarva.arsadditions.setup.registry;

import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

public class AddonCreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADDITIONS_TAB = TABS.register("general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ars_additions"))
            .icon(() -> AddonItemRegistry.ADVANCED_LECTERN_REMOTE.get().getDefaultInstance())
            .withTabsBefore(CreativeTabRegistry.BLOCKS.getKey())
            .build());
}
