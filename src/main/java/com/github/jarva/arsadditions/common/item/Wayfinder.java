package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Wayfinder extends Item {
    public Wayfinder() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains("Structure")) {
            String json = nbt.getString("Structure");
            tooltipComponents.add(Component.Serializer.fromJson(json).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains("Structure")) {
            return Util.makeDescriptionId("item", ArsAdditions.prefix("bound_wayfinder"));
        }
        return super.getDescriptionId(stack);
    }
}
