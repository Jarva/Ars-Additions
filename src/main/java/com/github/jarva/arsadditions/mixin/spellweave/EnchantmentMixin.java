package com.github.jarva.arsadditions.mixin.spellweave;

import com.github.jarva.arsadditions.datagen.EnchantmentDatagen;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.jarva.arsadditions.datagen.tags.ItemTagDatagen.SPELLWEAVE_INCOMPATIBLE;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "isSupportedItem", at = @At(value = "HEAD"), cancellable = true)
    private void isSupportedItem(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        HolderLookup.@Nullable RegistryLookup<Enchantment> enchantmentRegistry = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        if (enchantmentRegistry == null) return;

        Holder.Reference<Enchantment> enchantment = enchantmentRegistry.get(EnchantmentDatagen.SPELLWEAVE_ENCHANTMENT).orElse(null);
        if (enchantment != null && enchantment.value().equals(this)) {
            if (item.has(DataComponentRegistry.ARMOR_PERKS) || item.is(SPELLWEAVE_INCOMPATIBLE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
