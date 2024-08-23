package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.common.item.curios.Charm;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CharmChargingRecipe(ResourceLocation id, ResourceOrTag<Item> input, int costPerCharge) implements IImbuementRecipe {
    @Override
    public boolean matches(ImbuementTile imbuementTile, Level leve) {
        ItemStack reagent = imbuementTile.stack;
        if (reagent.getItem() instanceof Charm charm) {
            if (charm.getDamage(reagent) == 0) return false;
            return input.map(Ingredient::of, key -> Ingredient.of(BuiltInRegistries.ITEM.get(key)))
                    .map(ingredient -> ingredient.test(imbuementTile.stack)).orElse(false);
        }
        return false;
    }

    @Override
    public ItemStack assemble(ImbuementTile imbuementTile, HolderLookup.Provider provider) {
        ItemStack reagent = imbuementTile.stack;
        ItemStack result = reagent.copy();
        CharmRegistry.setCharges(result, result.getMaxDamage());
        return result;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.stack;
        return reagent.getDamageValue() * costPerCharge;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AddonRecipeRegistry.CHARM_CHARGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.CHARM_CHARGING_TYPE.get();
    }

    @Override
    public Component getCraftingStartedText(ImbuementTile imbuementTile) {
        return Component.translatable("chat.ars_additions.charm.charging_started", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingText(ImbuementTile imbuementTile) {
        return Component.translatable("tooltip.ars_additions.charm.charging", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingProgressText(ImbuementTile imbuementTile, int progress) {
        return Component.translatable("tooltip.ars_additions.charm.charging_progress", progress).withStyle(ChatFormatting.GOLD);
    }

    public static class Serializer implements RecipeSerializer<CharmChargingRecipe> {
        public static final MapCodec<CharmChargingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CharmChargingRecipe::id),
                ResourceOrTag.ITEM_CODEC.fieldOf("item").forGetter(CharmChargingRecipe::input),
                Codec.INT.optionalFieldOf("costPerDamage", 10).forGetter(CharmChargingRecipe::costPerCharge)
        ).apply(instance, CharmChargingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CharmChargingRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, CharmChargingRecipe::id,
                ResourceOrTag.ITEM_STREAM_CODEC, CharmChargingRecipe::input,
                ByteBufCodecs.INT, CharmChargingRecipe::costPerCharge,
                CharmChargingRecipe::new
        );

        @Override
        public MapCodec<CharmChargingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CharmChargingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
