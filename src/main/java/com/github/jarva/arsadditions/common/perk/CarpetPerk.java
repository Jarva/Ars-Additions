package com.github.jarva.arsadditions.common.perk;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class CarpetPerk extends Perk {
    public CarpetPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public boolean validForSlot(PerkSlot slot, ItemStack stack, Player player) {
        return super.validForSlot(slot, stack, player) && stack.is(AddonItemRegistry.MAGIC_CARPET.get());
    }
}
