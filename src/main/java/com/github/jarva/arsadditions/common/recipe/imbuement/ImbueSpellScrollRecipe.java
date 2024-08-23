package com.github.jarva.arsadditions.common.recipe.imbuement;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ImbueSpellScrollRecipe(ResourceLocation id) implements IImbuementRecipe {
    @Override
    public boolean matches(ImbuementTile imbuementTile, Level pLevel) {
        ItemStack reagent = imbuementTile.stack;

        if (!(reagent.getItem() instanceof ItemCasterProvider casterProvider)) return false;
        AbstractCaster<?> reagentCaster = casterProvider.getSpellCaster(reagent);
        return imbuementTile.getPedestalItems().isEmpty() && reagent.is(ItemsRegistry.SPELL_PARCHMENT.get()) && reagentCaster.getSpell().isValid();
    }

    @Override
    public ItemStack assemble(ImbuementTile imbuementTile, HolderLookup.Provider provider) {
        ItemStack result = new ItemStack(AddonItemRegistry.IMBUED_SPELL_PARCHMENT.get());
        result.set(DataComponentRegistry.SPELL_CASTER, imbuementTile.stack.get(DataComponentRegistry.SPELL_CASTER));
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        ItemStack reagent =  imbuementTile.stack;
        if (!(reagent.getItem() instanceof ItemCasterProvider casterProvider)) return 10000;
        return casterProvider.getSpellCaster(reagent).getSpell().getCost() * 10;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AddonRecipeRegistry.IMBUE_SCROLL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AddonRecipeRegistry.IMBUE_SCROLL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ImbueSpellScrollRecipe> {
        public static final MapCodec<ImbueSpellScrollRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ImbueSpellScrollRecipe::id)
        ).apply(instance, ImbueSpellScrollRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ImbueSpellScrollRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ImbueSpellScrollRecipe::id,
                ImbueSpellScrollRecipe::new
        );

        @Override
        public MapCodec<ImbueSpellScrollRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImbueSpellScrollRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
