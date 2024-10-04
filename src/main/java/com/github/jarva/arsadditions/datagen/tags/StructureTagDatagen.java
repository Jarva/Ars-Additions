package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.common.datagen.StructureTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class StructureTagDatagen extends TagsProvider<Structure> {
    public static TagKey<Structure> ON_EXPLORER_WARP_SCROLL = TagKey.create(Registries.STRUCTURE, new ResourceLocation(ArsAdditions.MODID, "on_explorer_warp_scroll"));
    public static TagKey<Structure> RUINED_PORTALS = TagKey.create(Registries.STRUCTURE, new ResourceLocation(ArsAdditions.MODID, "ruined_portals"));

    public StructureTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.STRUCTURE, future   , ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ON_EXPLORER_WARP_SCROLL)
                .addOptional(BuiltinStructures.STRONGHOLD.location())
                .addOptional(BuiltinStructures.ANCIENT_CITY.location())
                .addOptional(BuiltinStructures.DESERT_PYRAMID.location())
                .addOptional(BuiltinStructures.JUNGLE_TEMPLE.location())
                .addOptional(BuiltinStructures.PILLAGER_OUTPOST.location())
                .addOptionalTag(StructureTags.VILLAGE.location())
                .addTag(StructureTagProvider.WILDEN_DEN)
                .addOptional(BuiltinStructures.IGLOO.location())
                .addOptional(BuiltinStructures.TRAIL_RUINS.location())
                .addOptionalTag(StructureTags.OCEAN_RUIN.location())
                .addOptionalTag(StructureTags.SHIPWRECK.location())
                .addOptional(BuiltinStructures.SWAMP_HUT.location())
                .add(ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix("nexus_tower")));

        this.tag(RUINED_PORTALS)
                .add(ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix("ruined_portal")))
                .add(ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix("ruined_portal_large")));
    }
}
