package com.github.jarva.arsadditions.client.jei;

import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.ritual.RitualLocateStructure;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.client.jei.MultiInputCategory;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class LocateStructureRecipeCategory implements IRecipeCategory<LocateStructureRecipe> {

    public IDrawable background;
    public IDrawable icon;

    protected Vec2 point = new Vec2(48, 13);
    protected Vec2 center = new Vec2(48, 45);

    public LocateStructureRecipeCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(114, 108);
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.RITUAL_BLOCK));
    }

    @Override
    public RecipeType<LocateStructureRecipe> getRecipeType() {
        return ModPlugin.LOCATE_STRUCTURE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("item.ars_additions." + RitualLocateStructure.RESOURCE_LOCATION.getPath());
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
    public void setRecipe(IRecipeLayoutBuilder builder, LocateStructureRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, (int) center.x, (int) center.y).addIngredients(Ingredient.of(RitualRegistry.getRitualItemMap().get(RitualLocateStructure.RESOURCE_LOCATION)));

        List<Ingredient> inputs = recipe.getIngredients();
        double angleBetweenEach = 360.0 / inputs.size();
        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y).addIngredients(input);
            point = MultiInputCategory.rotatePointAbout(point, center, angleBetweenEach);
        }

        ItemStack wayfinder = new ItemStack(AddonItemRegistry.WAYFINDER.get());
        CompoundTag nbt = wayfinder.getOrCreateTag();
        Component name = recipe.getName();
        nbt.putString("Structure", Component.Serializer.toJson(name));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10).addIngredient(VanillaTypes.ITEM_STACK, wayfinder);
    }

    @Override
    public void draw(LocateStructureRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        Component name = recipe.getName();

        guiGraphics.drawString(renderer, name, 0, 100, 10, false);
    }
}

