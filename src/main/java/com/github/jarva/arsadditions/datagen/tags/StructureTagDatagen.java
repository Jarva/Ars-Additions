package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.common.datagen.StructureTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class StructureTagDatagen extends TagsProvider<Structure> {
    public static TagKey<Structure> ON_EXPLORER_WARP_SCROLL = create("on_explorer_warp_scroll");
    public static TagKey<Structure> RUINED_PORTALS = create("ruined_portals");

    public static TagKey<Structure> MONUMENT = create("monument");
    public static TagKey<Structure> DESERT_TEMPLE = create("desert_temple");
    public static TagKey<Structure> JUNGLE_TEMPLE = create("jungle_temple");
    public static TagKey<Structure> STRONGHOLD = create("stronghold");
    public static TagKey<Structure> NETHER_FORTRESS = create("nether_fortress");
    public static TagKey<Structure> ANCIENT_CITY = create("ancient_city");
    public static TagKey<Structure> PILLAGER_OUTPOST = create("pillager_outpost");
    public static TagKey<Structure> IGLOO = create("igloo");
    public static TagKey<Structure> TRAIL_RUINS = create("trail_ruins");
    public static TagKey<Structure> WITCH_HUT = create("witch_hut");
    public static TagKey<Structure> BASTION = create("bastion");
    public static TagKey<Structure> END_CITY = create("end_city");
    public static TagKey<Structure> TRIAL_CHAMBERS = create("trial_chambers");
    public static TagKey<Structure> WOODLAND_MANSION = create("woodland_mansion");

    public StructureTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.STRUCTURE, future, ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(MONUMENT)
                .addOptional(BuiltinStructures.OCEAN_MONUMENT.location())
                .addOptionalTag(parse("betteroceanmonuments:better_ocean_monuments"));
        this.tag(DESERT_TEMPLE)
                .addOptional(BuiltinStructures.DESERT_PYRAMID.location())
                .addOptionalTag(parse("betterdeserttemples:better_desert_temples"));
        this.tag(JUNGLE_TEMPLE)
                .addOptional(BuiltinStructures.JUNGLE_TEMPLE.location())
                .addOptionalTag(parse("betterjungletemples:better_jungle_temples"));
        this.tag(STRONGHOLD)
                .addOptional(BuiltinStructures.STRONGHOLD.location())
                .addOptionalTag(parse("betterstrongholds:better_strongholds"));
        this.tag(NETHER_FORTRESS)
                .addOptional(BuiltinStructures.FORTRESS.location())
                .addOptionalTag(parse("betterfortresses:better_fortresses"));
        this.tag(ANCIENT_CITY)
                .addOptional(BuiltinStructures.ANCIENT_CITY.location());
        this.tag(PILLAGER_OUTPOST)
                .addOptional(BuiltinStructures.PILLAGER_OUTPOST.location())
                .addOptionalTag(parse("minecraft:pillager_outpost"));
        this.tag(IGLOO)
                .addOptional(BuiltinStructures.IGLOO.location());
        this.tag(TRAIL_RUINS)
                .addOptional(BuiltinStructures.TRAIL_RUINS.location());
        this.tag(WITCH_HUT)
                .addOptional(BuiltinStructures.SWAMP_HUT.location())
                .addOptional(parse("betterwitchhuts:witch_hut"));
        this.tag(BASTION)
                .addOptional(BuiltinStructures.BASTION_REMNANT.location());
        this.tag(END_CITY)
                .addOptional(BuiltinStructures.END_CITY.location());
        this.tag(TRIAL_CHAMBERS)
                .addOptional(BuiltinStructures.TRIAL_CHAMBERS.location());
        this.tag(WOODLAND_MANSION)
                .addOptional(BuiltinStructures.WOODLAND_MANSION.location());

        this.tag(ON_EXPLORER_WARP_SCROLL)
                .addTag(MONUMENT)
                .addTag(DESERT_TEMPLE)
                .addTag(JUNGLE_TEMPLE)
                .addTag(STRONGHOLD)
                .addTag(NETHER_FORTRESS)
                .addTag(ANCIENT_CITY)
                .addTag(PILLAGER_OUTPOST)
                .addTag(IGLOO)
                .addTag(WITCH_HUT)
                .addTag(StructureTagProvider.WILDEN_DEN)
                .add(structure("nexus_tower"))
                .add(structure("arcane_library"));

        this.tag(RUINED_PORTALS)
                .add(structure("ruined_portal"))
                .add(structure("ruined_portal_large"));
    }

    public static TagKey<Structure> create(String name) {
        return TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix(name));
    }

    public static ResourceKey<Structure> structure(String name) {
        return ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix(name));
    }

    public static ResourceLocation parse(String name) {
        return ResourceLocation.parse(name);
    }
}
