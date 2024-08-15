package com.github.jarva.arsadditions.common.recipe.wixie;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.api.recipe.SingleRecipe;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.mojang.datafixers.kinds.App;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantingApparatusRecipeWrapper extends MultiRecipeWrapper {
    public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

    public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
        if (RECIPE_CACHE.containsKey(stack.getItem())) {
            return RECIPE_CACHE.get(stack.getItem());
        }

        MultiRecipeWrapper wrapper = new EnchantingApparatusRecipeWrapper();
        if (level.getServer() == null) return wrapper;

        for (RecipeHolder<?> recipe : level.getServer().getRecipeManager().getRecipes()) {
            if (recipe.value() instanceof EnchantingApparatusRecipe apparatusRecipe) {
                List<Ingredient> ingredients = new ArrayList<>(apparatusRecipe.pedestalItems());
                ItemStack result = apparatusRecipe.result();
                if (apparatusRecipe instanceof EnchantmentRecipe enchantmentRecipe) {
                    ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
                    Optional<Holder.Reference<Enchantment>> enchantment = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantmentRecipe.enchantmentKey);
                    if (enchantment.isEmpty() || !enchantment.get().isBound())
                        continue;
                    if (enchantments.getLevel(enchantment.get()) != enchantmentRecipe.enchantLevel)
                        continue;
                } else {
                    if (result.getItem() != stack.getItem())
                        continue;
                    ingredients.add(apparatusRecipe.reagent());
                }
                wrapper.addRecipe(ingredients, result, recipe.value());
            }
        }

        RECIPE_CACHE.put(stack.getItem(), wrapper);
        return wrapper;
    }

    @Override
    public @Nullable List<ItemStack> getItemsNeeded(Map<Item, Integer> inventory, Level world, BlockPos pos, SingleRecipe recipe) {
        List<ItemStack> items = new ArrayList<>();
        if (recipe.iRecipe instanceof EnchantmentRecipe enchantmentRecipe) {
            BlockEntity tile = world.getBlockEntity(pos);
            List<FilterableItemHandler> filterables = new ArrayList<>();
            if (tile instanceof WixieCauldronTile cauldronTile && world instanceof ServerLevel serverLevel) {
                for (BlockPos p : cauldronTile.getInventories()) {
                    BlockEntity be = world.getBlockEntity(p);
                    if (be == null) continue;
                    filterables.add(new FilterableItemHandler(serverLevel.getCapability(Capabilities.ItemHandler.BLOCK, pos, serverLevel.getBlockState(pos), be, null)));
                }
                InventoryManager inventoryManager = new InventoryManager(filterables);
                Player fakePlayer = ANFakePlayer.getPlayer(serverLevel);
                // TODO: Update Aparatus Input.
                SlotReference slot = inventoryManager.findItem(is -> is.is(Items.BOOK) || enchantmentRecipe.doesReagentMatch(new ApparatusRecipeInput(is, List.of(), fakePlayer), serverLevel, fakePlayer), InteractType.EXTRACT);
                if (slot.isEmpty()) return null;

                ItemStack found = slot.getHandler().getStackInSlot(slot.getSlot()).copy();
                items.add(found);

                ItemStack output = found.getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : found.copy();
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(output));
                Optional<Holder.Reference<Enchantment>> enchantment = world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantmentRecipe.enchantmentKey);
                if (enchantment.isEmpty() || !enchantment.get().isBound())
                    return null;
                enchantments.set(enchantment.get(), enchantmentRecipe.enchantLevel);
                EnchantmentHelper.setEnchantments(output, enchantments.toImmutable());
                recipe.outputStack = output;
            } else {
                return null;
            }
        }
        List<ItemStack> otherItems = super.getItemsNeeded(inventory, world, pos, recipe);
        if (otherItems == null) return null;
        items.addAll(otherItems);
        return items;
    }
}
