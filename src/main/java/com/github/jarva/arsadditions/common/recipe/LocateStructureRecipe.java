package com.github.jarva.arsadditions.common.recipe;

import com.github.jarva.arsadditions.common.loot.functions.ExplorationScrollFunction;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocateStructureRecipe implements Recipe<Container> {
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

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean matches(List<ItemStack> input) {
        return EnchantingApparatusRecipe.doItemsMatch(input, ingredients);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
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

    public JsonElement asRecipe() {
        JsonElement recipe = Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<LocateStructureRecipe> {
        public static final Codec<LocateStructureRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(LocateStructureRecipe::getId),
                ResourceOrTag.ITEM_CODEC.listOf().fieldOf("augments").forGetter(LocateStructureRecipe::getAugments),
                ResourceOrTag.STRUCTURE_CODEC.fieldOf("structure").forGetter(LocateStructureRecipe::getStructure),
                Codec.INT.optionalFieldOf("radius", ExplorationScrollFunction.DEFAULT_SEARCH_RADIUS).forGetter(LocateStructureRecipe::getRadius),
                Codec.BOOL.optionalFieldOf("skip_known_structures", ExplorationScrollFunction.DEFAULT_SKIP_EXISTING).forGetter(LocateStructureRecipe::getSkipExisting)
        ).apply(instance, LocateStructureRecipe::new));

        @Override
        public LocateStructureRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable LocateStructureRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, LocateStructureRecipe locateStructureRecipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, locateStructureRecipe);
        }
    }
}
