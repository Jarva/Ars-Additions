package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItemTagDatagen extends IntrinsicHolderTagsProvider<Item> {
    public static TagKey<Item> FORGOTTEN_KNOWLEDGE_GLYPHS = ItemTags.create(new ResourceLocation(ArsAdditions.MODID, "forgotten_knowledge"));
    public static TagKey<Item> BELTS = ItemTags.create(new ResourceLocation("curios", "belt"));
    public static TagKey<Item> CHARMS = ItemTags.create(new ResourceLocation("curios", "charm"));

    public ItemTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.ITEM, future, item -> item.builtInRegistryHolder().key(), ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(AddonItemRegistry.CODEX_ENTRY.get(), AddonItemRegistry.CODEX_ENTRY_LOST.get(), AddonItemRegistry.CODEX_ENTRY_ANCIENT.get(), ItemsRegistry.CASTER_TOME.get());
        this.tag(FORGOTTEN_KNOWLEDGE_GLYPHS);

        RitualTablet chunkLoading = RitualRegistry.getRitualItemMap().get(RitualChunkLoading.RESOURCE_LOCATION);
        this.tag(ItemTagProvider.RITUAL_TRADE_BLACKLIST).add(chunkLoading);
        this.tag(ItemTagProvider.RITUAL_LOOT_BLACKLIST).add(chunkLoading);

        this.tag(BELTS).add(AddonItemRegistry.LECTERN_REMOTE.get(), AddonItemRegistry.ADVANCED_LECTERN_REMOTE.get());

        for (Map.Entry<CharmRegistry.CharmType, RegistryObject<Item>> entry : AddonItemRegistry.CHARMS.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getName())).toList()) {
            this.tag(CHARMS).add(entry.getValue().get());
        }
    }
}
