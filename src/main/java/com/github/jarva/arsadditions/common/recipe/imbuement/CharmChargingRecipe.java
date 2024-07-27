package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record CharmChargingRecipe(ResourceLocation id, ResourceOrTag<Item> input, int costPerCharge) implements IImbuementRecipe {
    @Override
    public boolean isMatch(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.stack;
        if (!reagent.isDamaged()) return false;
        return input.map(Ingredient::of, key -> Ingredient.of(BuiltInRegistries.ITEM.get(key)))
            .map(ingredient -> ingredient.test(imbuementTile.stack)).orElse(false);
    }

    @Override
    public ItemStack getResult(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.getItem(0);
        ItemStack result = reagent.copy();
        CharmRegistry.setCharges(result, result.getMaxDamage());
        return result;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.getItem(0);
        return reagent.getDamageValue() * costPerCharge;
    }

    @Override
    public boolean matches(ImbuementTile container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(ImbuementTile container, RegistryAccess registryAccess) {
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
        return AddonRecipeRegistry.CHARM_CHARGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.CHARM_CHARGING_TYPE.get();
    }

    public JsonElement asRecipe() {
        JsonElement recipe = CharmChargingRecipe.Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    @Override
    public Component getCraftingStartedText(ImbuementTile imbuementTile) {
        return Component.translatable("chat.ars_additions.charm.charging_started", getResult(imbuementTile).getHoverName());
    }

    @Override
    public Component getCraftingText(ImbuementTile imbuementTile) {
        return Component.translatable("tooltip.ars_additions.charm.charging", getResult(imbuementTile).getHoverName());
    }

    @Override
    public Component getCraftingProgressText(ImbuementTile imbuementTile, int progress) {
        return Component.translatable("tooltip.ars_additions.charm.charging_progress", progress).withStyle(ChatFormatting.GOLD);
    }

    public static class Serializer implements RecipeSerializer<CharmChargingRecipe> {
        public static final Codec<CharmChargingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CharmChargingRecipe::id),
                ResourceOrTag.ITEM_CODEC.fieldOf("item").forGetter(CharmChargingRecipe::input),
                Codec.INT.optionalFieldOf("costPerDamage", 10).forGetter(CharmChargingRecipe::costPerCharge)
        ).apply(instance, CharmChargingRecipe::new));

        @Override
        public CharmChargingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable CharmChargingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, CharmChargingRecipe recipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
