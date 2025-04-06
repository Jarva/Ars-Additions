    package com.github.jarva.arsadditions.datagen.recipes;

    import com.github.jarva.arsadditions.ArsAdditions;
    import com.github.jarva.arsadditions.common.item.data.ExplorationScrollData;
    import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
    import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
    import com.github.jarva.arsadditions.datagen.tags.StructureTagDatagen;
    import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
    import com.hollingsworth.arsnouveau.common.datagen.StructureTagProvider;
    import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
    import com.mojang.serialization.JsonOps;
    import net.minecraft.core.registries.Registries;
    import net.minecraft.data.CachedOutput;
    import net.minecraft.data.DataGenerator;
    import net.minecraft.resources.ResourceKey;
    import net.minecraft.tags.ItemTags;
    import net.minecraft.tags.TagKey;
    import net.minecraft.world.item.Item;
    import net.minecraft.world.item.Items;
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
            Path path = getRecipePath(output, recipe.getId().getPath());
            LocateStructureRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).result().ifPresent(json -> {
                saveStable(pOutput, json, path);
            });
        }
    }

    protected void addEntries() {
        addEntry("pillager_outpost", StructureTagDatagen.PILLAGER_OUTPOST, ResourceOrTag.item(Items.EMERALD));
        addEntry("end_city", StructureTagDatagen.END_CITY, ResourceOrTag.item(Items.PURPUR_BLOCK));
        addEntry("jungle_temple", StructureTagDatagen.JUNGLE_TEMPLE, ResourceOrTag.item(Items.MOSSY_COBBLESTONE));
        addEntry("wilden_den", StructureTagProvider.WILDEN_DEN, ResourceOrTag.item(ItemsRegistry.SOURCE_GEM.get()));
        addEntry("monument", StructureTagDatagen.MONUMENT, ResourceOrTag.tag(ItemTags.FISHES));
        addEntry("fortress", StructureTagDatagen.NETHER_FORTRESS, ResourceOrTag.item(Items.NETHER_BRICK));
        addEntry("ancient_city", StructureTagDatagen.ANCIENT_CITY, ResourceOrTag.item(Items.DEEPSLATE_BRICKS));
        addEntry("igloo", StructureTagDatagen.IGLOO, ResourceOrTag.item(Items.ICE));
        addEntry("bastion", StructureTagDatagen.BASTION, ResourceOrTag.item(Items.POLISHED_BLACKSTONE_BRICKS));
        addEntry("desert_temple", StructureTagDatagen.DESERT_TEMPLE, ResourceOrTag.item(Items.SANDSTONE));
        addEntry("trail_ruins", StructureTagDatagen.TRAIL_RUINS, ResourceOrTag.tag(ItemTags.TERRACOTTA));
        addEntry("arcane_library", ResourceKey.create(Registries.STRUCTURE, ArsAdditions.prefix("arcane_library")), ResourceOrTag.item(ItemsRegistry.APPRENTICE_SPELLBOOK.get()));
        addEntry("stronghold", StructureTagDatagen.STRONGHOLD, ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE), ResourceOrTag.item(Items.ENDER_EYE));
        addEntry("trial_chamber", StructureTagDatagen.TRIAL_CHAMBERS, ResourceOrTag.item(Items.OMINOUS_BOTTLE));
        addEntry("woodland_mansion", StructureTagDatagen.WOODLAND_MANSION, ResourceOrTag.item(Items.BROWN_MUSHROOM), ResourceOrTag.item(Items.RED_MUSHROOM));
    }

    private void addEntry(String id, ResourceKey<Structure> structureId, ResourceOrTag<Item> ...augments) {
        addEntry(id, ResourceOrTag.key(structureId), augments);
    }

    private void addEntry(String id, TagKey<Structure> structureId, ResourceOrTag<Item> ...augments) {
        addEntry(id, ResourceOrTag.tag(structureId), augments);
    }

    private void addEntry(String id, ResourceOrTag<Structure> structure, ResourceOrTag<Item> ...augments) {
        recipes.add(new LocateStructureRecipe(ArsAdditions.prefix(id), List.of(augments), structure, ExplorationScrollData.DEFAULT_SEARCH_RADIUS, ExplorationScrollData.DEFAULT_SKIP_EXISTING));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipe/locate_structure/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Locate Structure Datagen";
    }
}
