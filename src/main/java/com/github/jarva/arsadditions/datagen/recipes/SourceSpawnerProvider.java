package com.github.jarva.arsadditions.datagen.recipes;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.SourceSpawnerRecipe;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.common.util.codec.TagModifier;
import com.github.jarva.arsadditions.datagen.tags.EntityTypeTagDatagen;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.github.jarva.arsadditions.setup.registry.ModifyTagRegistry;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.InventoryCarrier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SourceSpawnerProvider extends SimpleDataProvider {
    public List<SourceSpawnerRecipe> recipes = new ArrayList<>();

    public SourceSpawnerProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (SourceSpawnerRecipe recipe : recipes) {
            Path path = getRecipePath(output, AddonRecipeRegistry.SOURCE_SPAWNER_TYPE.getId().getPath());
            SourceSpawnerRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).result().ifPresent(json -> {
                saveStable(pOutput, json, path);
            });
        }
    }

    protected void addEntries() {
        addEntry("default", new ModifyTagRegistry.RemoveGuaranteedDrops(), new ModifyTagRegistry.RemoveTag(List.of(InventoryCarrier.TAG_INVENTORY, "Items")));
        addEntry("blacklist", ResourceOrTag.tag(EntityTypeTagDatagen.SOURCE_SPAWNER_NBT_BLACKLIST), null, List.of(new ModifyTagRegistry.RemoveTag(List.of())));
    }

    private void addEntry(String id, EntityType<?> entityType, TagModifier... tagModifiers) {
        addEntry(id, ResourceOrTag.key(entityType.builtInRegistryHolder().key()), null, List.of(tagModifiers));
    }

    private void addEntry(String id, TagModifier... tagModifiers) {
        addEntry(id, null, null, List.of(tagModifiers));
    }

    private void addEntry(String id, ResourceOrTag<EntityType<?>> entity, Integer source, List<TagModifier> tagModifiers) {
        recipes.add(
            new SourceSpawnerRecipe(
                ArsAdditions.prefix(id),
                Optional.ofNullable(entity),
                Optional.ofNullable(source),
                Optional.ofNullable(tagModifiers)
            )
        );
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipe/source_spawner/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Source Spawner Datagen";
    }
}
