package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.ISpellCasterProvider;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.items.ManipulationEssence;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BulkScribingRecipe(ResourceLocation id) implements IImbuementRecipe {
    @Override
    public boolean isMatch(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.stack;
        List<ItemStack> pedestalItems = imbuementTile.getPedestalItems();
        if (pedestalItems.size() != 1) return false;

        Optional<ItemStack> scriber = findScriber(imbuementTile);
        if (scriber.isEmpty()) return false;

        ItemStack scriberStack = scriber.get();
        ISpellCaster caster = CasterUtil.getCaster(scriberStack);
        if (caster == null || caster.getSpell().isEmpty()) return false;

        ISpellCaster reagentCaster = CasterUtil.getCaster(reagent);
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
    public ItemStack getResult(ImbuementTile imbuementTile) {
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
    public int getSourceCost(ImbuementTile imbuementTile) {
        Optional<ItemStack> scriber = findScriber(imbuementTile);
        if (scriber.isPresent() && scriber.get().getItem() instanceof ISpellCasterProvider provider) {
            return provider.getSpellCaster(scriber.get()).getSpell().getCost();
        }
        return 1000;
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
        return AddonRecipeRegistry.BULK_SCRIBING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.BULK_SCRIBING_TYPE.get();
    }

    public JsonElement asRecipe() {
        JsonElement recipe = BulkScribingRecipe.Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<BulkScribingRecipe> {
        public static final Codec<BulkScribingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(BulkScribingRecipe::id)
        ).apply(instance, BulkScribingRecipe::new));

        @Override
        public BulkScribingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable BulkScribingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, BulkScribingRecipe recipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
