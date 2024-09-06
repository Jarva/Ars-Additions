package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ImbueSpellScrollRecipe(ResourceLocation id) implements IImbuementRecipe {
    @Override
    public boolean isMatch(ImbuementTile imbuementTile) {
        if (imbuementTile.getPedestalItems().isEmpty()) return false;
        ItemStack reagent = imbuementTile.stack;
        if (!reagent.is(ItemsRegistry.SPELL_PARCHMENT.get())) return false;
        ISpellCaster reagentCaster = CasterUtil.getCaster(reagent);
        return reagentCaster.getSpell().isValid();
    }

    @Override
    public ItemStack getResult(ImbuementTile imbuementTile) {
        ItemStack result = new ItemStack(AddonItemRegistry.IMBUED_SPELL_PARCHMENT.get());
        result.setTag(imbuementTile.stack.getTag());
        return result;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        ItemStack reagent = imbuementTile.stack;
        ISpellCaster reagentCaster = CasterUtil.getCaster(reagent);
        return reagentCaster.getSpell().getCost() * 10;
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
        return AddonRecipeRegistry.IMBUE_SCROLL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.IMBUE_SCROLL_TYPE.get();
    }

    public JsonElement asRecipe() {
        JsonElement recipe = ImbueSpellScrollRecipe.Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<ImbueSpellScrollRecipe> {
        public static final Codec<ImbueSpellScrollRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ImbueSpellScrollRecipe::id)
        ).apply(instance, ImbueSpellScrollRecipe::new));

        @Override
        public ImbueSpellScrollRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable ImbueSpellScrollRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ImbueSpellScrollRecipe recipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
