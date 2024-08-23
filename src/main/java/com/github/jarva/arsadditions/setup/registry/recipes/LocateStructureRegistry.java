package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import net.minecraft.world.item.crafting.RecipeInput;

public class LocateStructureRegistry extends GenericRecipeRegistry<RecipeInput, LocateStructureRecipe> {
    public static LocateStructureRegistry INSTANCE;

    public LocateStructureRegistry() {
        super(AddonRecipeRegistry.LOCATE_STRUCTURE_TYPE);
    }
}
