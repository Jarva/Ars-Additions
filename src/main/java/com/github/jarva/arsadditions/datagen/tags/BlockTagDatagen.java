package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry.getBlocks;

public class BlockTagDatagen extends IntrinsicHolderTagsProvider<Block> {
    public BlockTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.BLOCK, future, item -> item.builtInRegistryHolder().key(), ArsAdditions.MODID, helper);
    }

    public static TagKey<Block> IMMOVABLE = BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", "immovable"));
    public static TagKey<Block> RELOCATION_NOT_SUPPORTED = BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", "relocation_not_supported"));
    public static TagKey<Block> CARRYON_BLACKLIST = BlockTags.create(ResourceLocation.fromNamespaceAndPath("carryon", "block_blacklist"));

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        Block[] decorative = getBlocks(AddonBlockNames.DECORATIVE_SOURCESTONES);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(decorative);
        this.tag(BlockTagProvider.DECORATIVE_AN).add(decorative);

        Block[] walls = getBlocks(AddonBlockNames.WALLS);
        this.tag(BlockTags.WALLS).add(walls);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(walls);

        Block[] buttons = getBlocks(AddonBlockNames.BUTTONS);
        this.tag(BlockTags.BUTTONS).add(buttons);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(buttons);

        Block[] lanterns = getBlocks(AddonBlockNames.LANTERNS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(lanterns);

        Block[] magelightLanterns = getBlocks(AddonBlockNames.MAGELIGHT_LANTERNS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(magelightLanterns);

        Block[] chains = getBlocks(AddonBlockNames.CHAINS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(chains);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(AddonBlockRegistry.WIXIE_ENCHANTING.get());

        Block[] doors = getBlocks(AddonBlockNames.DOORS);
        this.tag(BlockTags.DOORS).add(doors);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(doors);

        Block[] trapdoors = getBlocks(AddonBlockNames.TRAPDOORS);
        this.tag(BlockTags.TRAPDOORS).add(trapdoors);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(trapdoors);

        Block[] carpets = getBlocks(AddonBlockNames.CARPETS);
        this.tag(BlockTags.WOOL_CARPETS).add(carpets);

        this.tag(CARRYON_BLACKLIST).add(AddonBlockRegistry.WARP_NEXUS.get());
        this.tag(RELOCATION_NOT_SUPPORTED).add(AddonBlockRegistry.WARP_NEXUS.get());
        this.tag(IMMOVABLE).add(AddonBlockRegistry.WARP_NEXUS.get());
    }
}
