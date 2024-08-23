package com.github.jarva.arsadditions.datagen.recipes;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.imbuement.CharmChargingRecipe;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CharmChargingProvider extends SimpleDataProvider {
    public List<CharmChargingRecipe> recipes = new ArrayList<>();

    public CharmChargingProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        addEntries();
        for (CharmChargingRecipe recipe : recipes) {
            Path path = getRecipePath(output, recipe.id().getPath());
            CharmChargingRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).result().ifPresent(json -> {
                saveStable(pOutput, json, path);
            });
        }
    }

    protected void addEntries() {
        for (Map.Entry<CharmRegistry.CharmType, ItemRegistryWrapper<Item>> entry : AddonItemRegistry.CHARMS.entrySet()) {
            CharmRegistry.CharmType type = entry.getKey();
            addEntry(type.getSerializedName(), ResourceOrTag.item(entry.getValue().get()), type.getCostPerCharge());
        }
    }

    private void addEntry(String id, ResourceOrTag<Item> reagent, int costPerCharge) {
        recipes.add(new CharmChargingRecipe(ArsAdditions.prefix(id), reagent, costPerCharge));
    }

    protected static Path getRecipePath(Path path, String id) {
        return path.resolve("data/ars_additions/recipe/imbuement_charging/" + id + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Imbuement Charging Datagen";
    }
}
