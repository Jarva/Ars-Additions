package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ItemTagDatagen extends IntrinsicHolderTagsProvider<Item> {
    public static TagKey<Item> FORGOTTEN_KNOWLEDGE_GLYPHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsAdditions.MODID, "forgotten_knowledge"));
    public static TagKey<Item> BELTS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "belt"));
    public static TagKey<Item> CHARMS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "charm"));
    public static TagKey<Item> SPELLWEAVE_INCOMPATIBLE = ItemTags.create(ArsAdditions.prefix("spellweave_compatible"));

    public static final TagKey<Item> MAGIC_HOOD = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "hood"));
    public static final TagKey<Item> MAGIC_ROBE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "robe"));
    public static final TagKey<Item> MAGIC_LEG = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "legs"));
    public static final TagKey<Item> MAGIC_BOOT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "boot"));

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

        for (Map.Entry<CharmRegistry.CharmType, ItemRegistryWrapper<Item>> entry : AddonItemRegistry.CHARMS.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getName())).toList()) {
            this.tag(CHARMS).add(entry.getValue().get());
        }

        this.tag(MAGIC_HOOD)
                .add(ItemsRegistry.SORCERER_HOOD.asItem())
                .add(ItemsRegistry.ARCANIST_HOOD.asItem())
                .add(ItemsRegistry.BATTLEMAGE_HOOD.asItem());

        this.tag(MAGIC_ROBE)
                .add(ItemsRegistry.SORCERER_ROBES.asItem())
                .add(ItemsRegistry.ARCANIST_ROBES.asItem())
                .add(ItemsRegistry.BATTLEMAGE_ROBES.asItem());

        this.tag(MAGIC_LEG)
                .add(ItemsRegistry.SORCERER_LEGGINGS.asItem())
                .add(ItemsRegistry.ARCANIST_LEGGINGS.asItem())
                .add(ItemsRegistry.BATTLEMAGE_LEGGINGS.asItem());

        this.tag(MAGIC_BOOT)
                .add(ItemsRegistry.SORCERER_BOOTS.asItem())
                .add(ItemsRegistry.ARCANIST_BOOTS.asItem())
                .add(ItemsRegistry.BATTLEMAGE_BOOTS.asItem());

        this.tag(SPELLWEAVE_INCOMPATIBLE)
                .addOptionalTag(MAGIC_HOOD)
                .addOptionalTag(MAGIC_ROBE)
                .addOptionalTag(MAGIC_LEG)
                .addOptionalTag(MAGIC_BOOT);
    }
}
