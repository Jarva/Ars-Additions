package com.github.jarva.arsadditions.common.recipe;

import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SourceSpawnerRecipe(ResourceLocation id, EntityType<?> entity, Integer source) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    public boolean isMatch(EntityType<?> entity) {
        return entity.equals(this.entity);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
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

    public JsonElement asRecipe() {
        JsonElement recipe = Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        if (recipe == null) return null;
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<SourceSpawnerRecipe> {
        public static final Codec<SourceSpawnerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(SourceSpawnerRecipe::id),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SourceSpawnerRecipe::entity),
                Codec.INT.fieldOf("source").forGetter(SourceSpawnerRecipe::source)
        ).apply(instance, SourceSpawnerRecipe::new));

        @Override
        public SourceSpawnerRecipe fromJson(ResourceLocation pRecipeId, JsonObject json) {
            return CODEC.parse(JsonOps.INSTANCE, json).result().orElse(null);
        }

        @Override
        public @Nullable SourceSpawnerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SourceSpawnerRecipe recipe) {
            buffer.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
