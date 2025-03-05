package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.datagen.EnchantmentDatagen;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.jarva.arsadditions.datagen.tags.ItemTagDatagen.SPELLWEAVE_INCOMPATIBLE;

@Mixin(IItemStackExtension.class)
public interface ItemStackExtensionMixin extends IItemStackExtension {
    @Shadow
    ItemStack self();

    @Inject(method = "supportsEnchantment", at = @At(value = "HEAD"), cancellable = true)
    private void supportEnchantment(Holder<Enchantment> enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (enchantment.is(EnchantmentDatagen.SPELLWEAVE_ENCHANTMENT)) {
            if (this.self().is(SPELLWEAVE_INCOMPATIBLE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
