package com.github.jarva.arsadditions.common.recipe.wixie;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.api.recipe.SingleRecipe;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantingApparatusRecipeWrapper extends MultiRecipeWrapper {
    public static Map<Item, MultiRecipeWrapper> RECIPE_CACHE = new HashMap<>();

    public static MultiRecipeWrapper fromStack(ItemStack stack, Level level) {
        if (RECIPE_CACHE.containsKey(stack.getItem())) {
            return RECIPE_CACHE.get(stack.getItem());
        }

        MultiRecipeWrapper wrapper = new EnchantingApparatusRecipeWrapper();
        if (level.getServer() == null) return wrapper;

        for (Recipe<?> recipe : level.getServer().getRecipeManager().getRecipes()) {
            if (recipe instanceof EnchantingApparatusRecipe apparatusRecipe) {
                List<Ingredient> ingredients = new ArrayList<>(apparatusRecipe.pedestalItems);
                ItemStack result = apparatusRecipe.result;
                if (recipe instanceof EnchantmentRecipe enchantmentRecipe) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                    if (!enchantments.containsKey(enchantmentRecipe.enchantment) || enchantments.get(enchantmentRecipe.enchantment) != enchantmentRecipe.enchantLevel)
                        continue;
                } else {
                    if (result.getItem() != stack.getItem())
                        continue;
                    ingredients.add(apparatusRecipe.reagent);
                }
                wrapper.addRecipe(ingredients, result, recipe);
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
                    filterables.add(InvUtil.getFilteredHandler(be));
                }
                InventoryManager inventoryManager = new InventoryManager(filterables);
                SlotReference slot = inventoryManager.findItem(is -> is.is(Items.BOOK) || enchantmentRecipe.doesReagentMatch(is, ANFakePlayer.getPlayer(serverLevel)), InteractType.EXTRACT);
                if (slot.isEmpty()) return null;

                ItemStack found = slot.getHandler().getStackInSlot(slot.getSlot()).copy();
                items.add(found);

                ItemStack output = found.getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : found.copy();
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
                enchantments.put(enchantmentRecipe.enchantment, enchantmentRecipe.enchantLevel);
                EnchantmentHelper.setEnchantments(enchantments, output);
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
