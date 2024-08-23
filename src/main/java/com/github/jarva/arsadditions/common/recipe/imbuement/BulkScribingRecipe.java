package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.items.ManipulationEssence;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public record BulkScribingRecipe(ResourceLocation id) implements IImbuementRecipe {

    @Override
    public boolean matches(ImbuementTile imbuementTile, Level pLevel) {
        ItemStack reagent = imbuementTile.stack;
        List<ItemStack> pedestalItems = imbuementTile.getPedestalItems();
        if (pedestalItems.size() != 1) return false;

        Optional<ItemStack> scriber = findScriber(imbuementTile);
        if (scriber.isEmpty()) return false;

        ItemStack scriberStack = scriber.get();
        if (!(scriberStack.getItem() instanceof ItemCasterProvider itemCaster)) {
            return false;
        }
        AbstractCaster<?> caster = itemCaster.getSpellCaster(scriberStack);
        if (caster == null || caster.getSpell().isEmpty()) return false;

        if (!(reagent.getItem() instanceof ItemCasterProvider reagentItemCaster)) {
            return false;
        }
        AbstractCaster<?> reagentCaster = reagentItemCaster.getSpellCaster(reagent);
        if (reagentCaster == null || !reagentCaster.getSpell().isEmpty()) return false;

        return reagent.getItem() instanceof IScribeable;
    }

    public Optional<ItemStack> findScriber(ImbuementTile imbuementTile) {
        return imbuementTile.getPedestalItems().stream().filter(this::isScriber).findFirst();
    }

    public boolean isScriber(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SpellBook || item instanceof SpellParchment || item instanceof ManipulationEssence;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        Optional<ItemStack> scriber = findScriber(imbuementTile);
        if (scriber.isPresent() && scriber.get().getItem() instanceof ItemCasterProvider provider) {
            return provider.getSpellCaster(scriber.get()).getSpell().getCost();
        }
        return 1000;
    }

    @Override
    public ItemStack assemble(ImbuementTile imbuementTile, HolderLookup.Provider provider) {
        ItemStack result = imbuementTile.stack.copy();
        if (result.is(ItemsRegistry.BLANK_PARCHMENT.get())) {
            result = new ItemStack(ItemsRegistry.SPELL_PARCHMENT.get());
        }
        if (result.getItem() instanceof IScribeable scribeable && imbuementTile.getLevel() instanceof ServerLevel serverLevel) {
            Optional<ItemStack> scriber = findScriber(imbuementTile);
            if (scriber.isPresent()) {
                ItemStack is = scriber.get();
                ANFakePlayer player = ANFakePlayer.getPlayer(serverLevel);
                player.setItemInHand(InteractionHand.MAIN_HAND, is);
                scribeable.onScribe(imbuementTile.getLevel(), imbuementTile.getBlockPos(), player, InteractionHand.MAIN_HAND, result);
            }
        }
        return result;
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
        return AddonRecipeRegistry.BULK_SCRIBING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.BULK_SCRIBING_TYPE.get();
    }

    @Override
    public Component getCraftingStartedText(ImbuementTile imbuementTile) {
        return Component.translatable("chat.ars_additions.imbued_spell_parchment.scribing_started", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingText(ImbuementTile imbuementTile) {
        Optional<ItemStack> scriber = findScriber(imbuementTile);
        if (scriber.isPresent() && scriber.get().getItem() instanceof ItemCasterProvider provider) {
            return Component.translatable("tooltip.ars_additions.imbued_spell_parchment.scribing", provider.getSpellCaster(scriber.get()).getSpell().getDisplayString());
        }
        return Component.translatable("tooltip.ars_additions.imbued_spell_parchment.scribing", assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    @Override
    public Component getCraftingProgressText(ImbuementTile imbuementTile, int progress) {
        return Component.translatable("tooltip.ars_additions.imbued_spell_parchment.scribing_progress", progress).withStyle(ChatFormatting.GOLD);
    }

    public static class Serializer implements RecipeSerializer<BulkScribingRecipe> {
        public static final MapCodec<BulkScribingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(BulkScribingRecipe::id)
        ).apply(instance, BulkScribingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BulkScribingRecipe> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, BulkScribingRecipe::id, BulkScribingRecipe::new);

        @Override
        public MapCodec<BulkScribingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BulkScribingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
