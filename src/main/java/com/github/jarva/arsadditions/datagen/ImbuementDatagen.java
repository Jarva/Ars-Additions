package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ImbuementRecipeProvider;
import com.hollingsworth.arsnouveau.common.datagen.ModDatagen;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ImbuementDatagen extends ImbuementRecipeProvider {
    public ImbuementDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

    protected static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/" + Setup.root + "/recipe/imbuement/" + str + ".json");
    }

    public void collectJsons(CachedOutput pOutput) {
        for (CharmRegistry.CharmType value : CharmRegistry.CharmType.values()) {
            recipes.add(new ImbuementRecipe(
                    ArsAdditions.prefix(value.getSerializedName()).withPrefix("charms/"),
                    Ingredient.of(AddonItemRegistry.CHARMS.get(value).get()),
                    AddonItemRegistry.CHARMS.get(value).get().getDefaultInstance(),
                    2000)
            );
        }
    }

    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.collectJsons(pOutput);
        List<CompletableFuture<?>> futures = new ArrayList<>();
        return ModDatagen.registries.thenCompose((registry) -> {

            for (ImbuementRecipe g : this.recipes) {
                Path path = getRecipePath(this.output, g.id.getPath());
                futures.add(DataProvider.saveStable(pOutput, registry, ImbuementRecipe.CODEC, g, path));
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        });
    }
}
