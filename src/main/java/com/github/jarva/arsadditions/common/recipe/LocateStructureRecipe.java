package com.github.jarva.arsadditions.common.recipe;

import com.github.jarva.arsadditions.common.item.data.ExplorationScrollData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class LocateStructureRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final ResourceLocation id;
    private final List<ResourceOrTag<Item>> augments;
    private final ResourceOrTag<Structure> structure;
    private final boolean skipKnownStructures;
    private final int radius;

    public LocateStructureRecipe(ResourceLocation id, List<ResourceOrTag<Item>> augments, ResourceOrTag<Structure> structure, int radius, boolean skipKnownStructures) {
        this.id = id;
        this.augments = augments;
        this.structure = structure;
        this.radius = radius;
        this.skipKnownStructures = skipKnownStructures;

        List<Ingredient> ingredientList = new ArrayList<>();
        augments.forEach(either ->
                either.tag().map(Ingredient::of).or(() -> either.key().map(BuiltInRegistries.ITEM::get).map(Ingredient::of)).ifPresent(ingredientList::add)
        );
        this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredientList.toArray(new Ingredient[]{}));
    }

    public boolean matches(List<ItemStack> input) {
        return EnchantingApparatusRecipe.doItemsMatch(input, ingredients);
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    private List<ResourceOrTag<Item>> getAugments() {
        return augments;
    }

    public ResourceOrTag<Structure> getStructure() {
        return structure;
    }

    public Component getName() {
        return structure.map(key -> key.location().getPath(), tag -> tag.location().getPath())
            .map(LangUtil::toTitleCase)
            .orElse(Component.empty());
    }

    public HolderSet<Structure> getStructureHolder(ServerLevel level) {
        return structure.tag()
            .map(tag -> LocateUtil.holderFromTag(level, tag))
            .or(() -> structure.key().map(value -> LocateUtil.holderFromResource(level, value)))
            .orElse(null);
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getRadius() {
        return radius;
    }

    public boolean getSkipExisting() {
        return skipKnownStructures;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AddonRecipeRegistry.LOCATE_STRUCTURE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.LOCATE_STRUCTURE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<LocateStructureRecipe> {
        public static final MapCodec<LocateStructureRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(LocateStructureRecipe::getId),
                ResourceOrTag.ITEM_CODEC.codec().listOf().fieldOf("augments").forGetter(LocateStructureRecipe::getAugments),
                ResourceOrTag.STRUCTURE_CODEC.fieldOf("structure").forGetter(LocateStructureRecipe::getStructure),
                Codec.INT.optionalFieldOf("radius", ExplorationScrollData.DEFAULT_SEARCH_RADIUS).forGetter(LocateStructureRecipe::getRadius),
                Codec.BOOL.optionalFieldOf("skip_known_structures", ExplorationScrollData.DEFAULT_SKIP_EXISTING).forGetter(LocateStructureRecipe::getSkipExisting)
        ).apply(instance, LocateStructureRecipe::new));

        @Override
        public MapCodec<LocateStructureRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LocateStructureRecipe> streamCodec() {
            return CheatSerializer.create(CODEC);
        }
    }
}
