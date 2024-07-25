package com.github.jarva.arsadditions.common.recipe;

import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.common.util.codec.TagModifier;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.github.jarva.arsadditions.setup.registry.ModifyTagRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record SourceSpawnerRecipe(ResourceLocation id, Optional<ResourceOrTag<EntityType<?>>> entity, Optional<Integer> source, Optional<List<TagModifier>> tag_modifiers) implements Recipe<RecipeInput> {
    public boolean isMatch(EntityType<?> entity) {
        return this.entity.map(entityTypeResourceOrTag -> entityTypeResourceOrTag.map(
                entity::is,
                key -> EntityType.getKey(entity).equals(key.location())
        ).orElse(false)).orElse(true);
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
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AddonRecipeRegistry.SOURCE_SPAWNER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.SOURCE_SPAWNER_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<SourceSpawnerRecipe> {
        public static final MapCodec<SourceSpawnerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(SourceSpawnerRecipe::id),
                ResourceOrTag.ENTITY_TYPE_CODEC.optionalFieldOf("entity").forGetter(SourceSpawnerRecipe::entity),
                Codec.INT.optionalFieldOf("source").forGetter(SourceSpawnerRecipe::source),
                ModifyTagRegistry.TAG_MODIFIER_DISPATCHER.dispatchedCodec().listOf().optionalFieldOf("tag_modifiers").forGetter(SourceSpawnerRecipe::tag_modifiers)
        ).apply(instance, SourceSpawnerRecipe::new));

        @Override
        public MapCodec<SourceSpawnerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SourceSpawnerRecipe> streamCodec() {
            return CheatSerializer.create(CODEC);
        }
    }
}
