package com.github.jarva.arsadditions.datagen.recipes;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.loot.functions.ExplorationScrollFunction;
import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import com.hollingsworth.arsnouveau.common.datagen.StructureTagProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocateStructureProvider extends SimpleDataProvider {
    public List<LocateStructureRecipe> recipes = new ArrayList<>();

    public LocateStructureProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (LocateStructureRecipe recipe : recipes) {
            Path path = getRecipePath(output, AddonRecipeRegistry.LOCATE_STRUCTURE_TYPE.getId().getPath());
            LocateStructureRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).result().ifPresent(json -> {
                saveStable(pOutput, json, path);
            });
        }
    }

    protected void addEntries() {
        addEntry("pillager_outpost", BuiltinStructures.PILLAGER_OUTPOST, ResourceOrTag.item(Items.EMERALD));
        addEntry("end_city", BuiltinStructures.END_CITY, ResourceOrTag.item(Items.PURPUR_BLOCK));
        addEntry("jungle_temple", BuiltinStructures.JUNGLE_TEMPLE, ResourceOrTag.item(Items.MOSSY_COBBLESTONE));
        addEntry("wilden_den", StructureTagProvider.WILDEN_DEN, ResourceOrTag.item(ItemsRegistry.SOURCE_GEM.get()));
        addEntry("monument", BuiltinStructures.OCEAN_MONUMENT, ResourceOrTag.tag(ItemTags.FISHES));
        addEntry("fortress", BuiltinStructures.FORTRESS, ResourceOrTag.item(Items.NETHER_BRICK));
        addEntry("ancient_city", BuiltinStructures.ANCIENT_CITY, ResourceOrTag.item(Items.DEEPSLATE_BRICKS));
        addEntry("igloo", BuiltinStructures.IGLOO, ResourceOrTag.item(Items.ICE));
        addEntry("bastion", BuiltinStructures.BASTION_REMNANT, ResourceOrTag.item(Items.POLISHED_BLACKSTONE_BRICKS));
        addEntry("desert_temple", BuiltinStructures.DESERT_PYRAMID, ResourceOrTag.item(Items.SANDSTONE));
        addEntry("trail_ruins", BuiltinStructures.TRAIL_RUINS, ResourceOrTag.tag(ItemTags.TERRACOTTA));
        addEntry("arcane_library", ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix("arcane_library")), ResourceOrTag.item(ItemsRegistry.APPRENTICE_SPELLBOOK.get()));
    }

    private void addEntry(String id, ResourceKey<Structure> structureId, ResourceOrTag<Item> ...augments) {
        addEntry(id, ResourceOrTag.key(structureId), augments);
    }

    private void addEntry(String id, TagKey<Structure> structureId, ResourceOrTag<Item> ...augments) {
        addEntry(id, ResourceOrTag.tag(structureId), augments);
    }

    private void addEntry(String id, ResourceOrTag<Structure> structure, ResourceOrTag<Item> ...augments) {
        recipes.add(new LocateStructureRecipe(ArsAdditions.prefix(id), List.of(augments), structure, ExplorationScrollFunction.DEFAULT_SEARCH_RADIUS, ExplorationScrollFunction.DEFAULT_SKIP_EXISTING));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipes/locate_structure/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Locate Structure Datagen";
    }
}
