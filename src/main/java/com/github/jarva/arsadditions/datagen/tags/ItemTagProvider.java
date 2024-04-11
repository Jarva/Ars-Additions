package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends IntrinsicHolderTagsProvider<Item> {
    public static TagKey<Item> FORGOTTEN_KNOWLEDGE_GLYPHS = ItemTags.create(new ResourceLocation(ArsAdditions.MODID, "forgotten_knowledge"));
    public ItemTagProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.ITEM, future, item -> item.builtInRegistryHolder().key(), ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(AddonItemRegistry.CODEX_ENTRY.get(), AddonItemRegistry.CODEX_ENTRY_LOST.get(), AddonItemRegistry.CODEX_ENTRY_ANCIENT.get());
        this.tag(FORGOTTEN_KNOWLEDGE_GLYPHS);
    }
}
