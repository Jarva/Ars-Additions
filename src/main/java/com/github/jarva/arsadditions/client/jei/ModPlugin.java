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
import mezz.jei.library.gui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModPlugin implements IModPlugin {
    public static final RecipeType<LocateStructureRecipe> LOCATE_STRUCTURE_RECIPE_TYPE = RecipeType.create(ArsAdditions.MODID, "locate_structure", LocateStructureRecipe.class);
    public static final RecipeType<CharmChargingRecipe> CHARM_CHARGING_RECIPE_TYPE = RecipeType.create(ArsAdditions.MODID, "charm_charging", CharmChargingRecipe.class);

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

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<LocateStructureRecipe> locateStructureRecipes = new ArrayList<>();
        List<CharmChargingRecipe> charmChargingRecipes = new ArrayList<>();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        for (RecipeHolder<?> i : manager.getRecipes()) {
            switch (i.value()) {
                case LocateStructureRecipe recipe -> locateStructureRecipes.add(recipe);
                case CharmChargingRecipe recipe -> charmChargingRecipes.add(recipe);
                default -> {}
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
