package com.github.jarva.arsadditions.client.jei;

import com.github.jarva.arsadditions.common.recipe.imbuement.CharmChargingRecipe;
import com.hollingsworth.arsnouveau.client.jei.MultiInputCategory;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class CharmChargingRecipeCategory extends MultiInputCategory<CharmChargingRecipe> {
    public IDrawable background;
    public IDrawable icon;

    public CharmChargingRecipeCategory(IGuiHelper helper) {
        super(helper, imbuementRecipe -> {
            var stack = imbuementRecipe.input().getDefaultInstance();

            return new MultiProvider(stack, List.of(), Ingredient.of(stack));
        });
        background = helper.createBlankDrawable(126, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.IMBUEMENT_BLOCK));
    }

    @Override
    public RecipeType<RecipeHolder<CharmChargingRecipe>> getRecipeType() {
        return ModPlugin.CHARM_CHARGING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_additions.charm_recharging");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(RecipeHolder<CharmChargingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        CharmChargingRecipe recipe = recipeHolder.value();
        Font renderer = Minecraft.getInstance().font;
        guiGraphics.drawString(renderer,  Component.translatable("ars_additions.source_per_charge", recipe.costPerCharge()), 0, 100, 10,false);
    }
}
