package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.github.jarva.arsadditions.datagen.EnchantmentDatagen.SPELLWEAVE_ENCHANTMENT;

@Mixin(AlterationTable.class)
public class AlterationTableMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/util/PerkUtil;getPerkHolder(Lnet/minecraft/world/item/ItemStack;)Lcom/hollingsworth/arsnouveau/common/items/data/ArmorPerkHolder;"))
    private ArmorPerkHolder useItemOn(ItemStack stack, Operation<ArmorPerkHolder> original, @Local(argsOnly = true) Level level) {
        ArmorPerkHolder def = original.call(stack);

        if (!(stack.getItem() instanceof ArmorItem)) {
            return def;
        }

        Integer enchantment = level.registryAccess().lookup(Registries.ENCHANTMENT)
                .flatMap(registry -> registry.get(SPELLWEAVE_ENCHANTMENT))
                .map(stack::getEnchantmentLevel)
                .orElse(null);

        if (enchantment == null || enchantment <= 0) return def;

        stack.set(AddonDataComponentRegistry.OVERRIDE_PERKS, true);

        return stack.update(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(), (holder) -> {
            holder.setTier(enchantment);
            return holder;
        });
    }
}
