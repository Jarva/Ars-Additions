package com.github.jarva.arsadditions.mixin.spellweave;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import com.github.jarva.arsadditions.common.perk.CarpetPerk;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.AlterationTile;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AlterationTile.class)
public class AlterationTileCarpetMixin {
    @Shadow
    public ItemStack armorStack;

    @Shadow
    public List<ItemStack> perkList;

    @Inject(method = "addPerkStack", at = @At("HEAD"), cancellable = true)
    private void ars_additions$preventDuplicateCarpetThreads(ItemStack stack, Player player, CallbackInfo ci) {
        if (!this.armorStack.is(AddonItemRegistry.MAGIC_CARPET.get())) {
            return;
        }
        if (!(stack.getItem() instanceof PerkItem incomingPerkItem) || !(incomingPerkItem.perk instanceof CarpetPerk)) {
            return;
        }

        boolean duplicate = this.perkList.stream()
                .map(existing -> existing.getItem() instanceof PerkItem perkItem ? perkItem.perk : null)
                .anyMatch(existingPerk -> existingPerk != null && existingPerk.equals(incomingPerkItem.perk));
        if (!duplicate) {
            return;
        }

        PortUtil.sendMessage(player, Component.translatable("chat.ars_additions.magic_carpet.duplicate_thread"));
        ci.cancel();
    }

    @WrapOperation(
            method = "removeArmorStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addItem(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean ars_additions$spillOverflowOnDowngrade(Player player, ItemStack stack, Operation<Boolean> original) {
        if (stack.is(AddonItemRegistry.MAGIC_CARPET.get())) {
            AlterationTile tile = (AlterationTile) (Object) this;
            if (tile.getLevel() != null) {
                MagicCarpetEntity.spillOverflowAfterCapacityDrop(stack, this.armorStack, tile.getLevel(), tile.getBlockPos().getCenter());
            }
        }
        return original.call(player, stack);
    }
}
