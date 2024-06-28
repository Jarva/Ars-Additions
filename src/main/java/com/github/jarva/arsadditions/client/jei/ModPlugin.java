package com.github.jarva.arsadditions.client.jei;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.ritual.RitualLocateStructure;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModPlugin implements IModPlugin {
    public static final RecipeType<LocateStructureRecipe> LOCATE_STRUCTURE_RECIPE_TYPE = RecipeType.create(ArsAdditions.MODID, "locate_structure", LocateStructureRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ArsAdditions.prefix("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new LocateStructureRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<LocateStructureRecipe> locateStructureRecipes = new ArrayList<>();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (Recipe<?> i : manager.getRecipes()) {
            if (i instanceof LocateStructureRecipe recipe) {
                locateStructureRecipes.add(recipe);
            }
        }

        registration.addRecipes(LOCATE_STRUCTURE_RECIPE_TYPE, locateStructureRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.RITUAL_BLOCK), LOCATE_STRUCTURE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(RitualRegistry.getRitualItemMap().get(RitualLocateStructure.RESOURCE_LOCATION)), LOCATE_STRUCTURE_RECIPE_TYPE);
    }
}
