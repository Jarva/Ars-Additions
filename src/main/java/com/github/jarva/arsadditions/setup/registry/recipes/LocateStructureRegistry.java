package com.github.jarva.arsadditions.setup.registry.recipes;

import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import net.minecraft.world.Container;

public class LocateStructureRegistry extends GenericRecipeRegistry<Container, LocateStructureRecipe> {
    public static LocateStructureRegistry INSTANCE = new LocateStructureRegistry();

    public LocateStructureRegistry() {
        super(AddonRecipeRegistry.LOCATE_STRUCTURE_TYPE);
    }
}
