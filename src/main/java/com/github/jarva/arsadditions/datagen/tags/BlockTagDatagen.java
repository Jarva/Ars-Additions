package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry.getBlocks;

public class BlockTagDatagen extends IntrinsicHolderTagsProvider<Block> {
    public BlockTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.BLOCK, future, item -> item.builtInRegistryHolder().key(), ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        Block[] decorative = getBlocks(AddonBlockNames.DECORATIVE_SOURCESTONES);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(decorative);

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
    }
}
