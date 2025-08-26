package com.github.jarva.arsadditions.mixin.spellweave;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;

@Mixin(PerkRegistry.class)
public class PerkRegistryMixin {
    @WrapOperation(method = "getPerkProvider(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/registry/PerkRegistry;getPerkProvider(Lnet/minecraft/world/item/Item;)Ljava/util/List;"))
    private static List<List<PerkSlot>> getPerkProvider(Item item, Operation<List<List<PerkSlot>>> original, @Local(argsOnly = true) ItemStack itemStack) {
        Boolean shouldOverride = itemStack.getOrDefault(AddonDataComponentRegistry.OVERRIDE_PERKS, false);
        if (shouldOverride) {
            return PerkSlot.PERK_SLOTS.values().stream().sorted(Comparator.comparingInt(PerkSlot::value)).map(List::of).toList();
        }
        return original.call(item);
    }
}
