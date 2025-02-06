package com.github.jarva.arsadditions.common.perk;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class ReachPerk extends Perk {
    public static final ReachPerk INSTANCE = new ReachPerk();

    public ReachPerk() {
        super(ArsAdditions.prefix("thread_reach"));
    }

    @Override
    public String getLangName() {
        return "Reach";
    }

    @Override
    public String getLangDescription() {
        return "Increases entity and block interaction distances by 1 for each level.";
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers
                .withModifierAdded(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(INSTANCE.getRegistryName(), slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup)
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(INSTANCE.getRegistryName(), slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }
}
