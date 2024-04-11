package com.github.jarva.arsadditions.mixin;


import com.github.jarva.arsadditions.item.UnstableReliquary;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public Container container;

    @Inject(method = "set", at = @At(value = "HEAD"))
    private void setHook(ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof UnstableReliquary && !(container instanceof Inventory)) {
            UnstableReliquary.breakReliquary(stack);
        }
    }
}
