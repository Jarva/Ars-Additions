package com.github.jarva.arsadditions.client.jei;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.recipe.imbuement.CharmChargingRecipe;
import com.github.jarva.arsadditions.common.ritual.RitualLocateStructure;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<LocateStructureRecipe>> LOCATE_STRUCTURE_RECIPE_TYPE = RecipeType.createRecipeHolderType(ArsAdditions.prefix("locate_structure"));
    public static final RecipeType<RecipeHolder<CharmChargingRecipe>> CHARM_CHARGING_RECIPE_TYPE = RecipeType.createRecipeHolderType(ArsAdditions.prefix("charm_charging"));

    @Override
    public ResourceLocation getPluginUid() {
        return ArsAdditions.prefix("main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new LocateStructureRecipeCategory(helper),
                new CharmChargingRecipeCategory(helper)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RecipeHolder<LocateStructureRecipe>> locateStructureRecipes = new ArrayList<>();
        List<RecipeHolder<CharmChargingRecipe>> charmChargingRecipes = new ArrayList<>();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (RecipeHolder<?> h : manager.getRecipes()) {
            Recipe<?> i = h.value();
            if (i instanceof LocateStructureRecipe) {
                locateStructureRecipes.add((RecipeHolder<LocateStructureRecipe>) h);
            }
            if (i instanceof CharmChargingRecipe) {
                charmChargingRecipes.add((RecipeHolder<CharmChargingRecipe>) h);
            }
        }

        registration.addRecipes(LOCATE_STRUCTURE_RECIPE_TYPE, locateStructureRecipes);
        registration.addRecipes(CHARM_CHARGING_RECIPE_TYPE, charmChargingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.RITUAL_BLOCK), LOCATE_STRUCTURE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(RitualRegistry.getRitualItemMap().get(RitualLocateStructure.RESOURCE_LOCATION)), LOCATE_STRUCTURE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.IMBUEMENT_BLOCK), CHARM_CHARGING_RECIPE_TYPE);
    }
}
