package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.common.item.ImbuedSpellParchment;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.registry.CasterTomeRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(CasterTomeRegistry.class)
public class CasterTomeRegistryMixin {
    @Unique
    private static List<Supplier<ItemStack>> arsAdditions$originalBasicLoot = null;

    @Inject(method = "reloadTomeData", at = @At("RETURN"), remap = false)
    private static void addImbuedParchmentLoot(CallbackInfoReturnable<?> cir) {
        if (arsAdditions$originalBasicLoot == null) {
            arsAdditions$originalBasicLoot = new ArrayList<>(DungeonLootTables.BASIC_LOOT);
        }

        DungeonLootTables.BASIC_LOOT.clear();
        DungeonLootTables.BASIC_LOOT.addAll(arsAdditions$originalBasicLoot);

        for (RecipeHolder<CasterTomeData> tomeRecipe : CasterTomeRegistry.getTomeData()) {
            DungeonLootTables.BASIC_LOOT.add(() -> ImbuedSpellParchment.fromCasterTome(tomeRecipe.value()));
        }
    }
}
