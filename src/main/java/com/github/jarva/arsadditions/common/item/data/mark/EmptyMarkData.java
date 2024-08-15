package com.github.jarva.arsadditions.common.item.data.mark;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EmptyMarkData implements MarkData {
    public static final EmptyMarkData INSTANCE = new EmptyMarkData();
    public static final MapCodec<EmptyMarkData> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.empty"));
    }

    @Override
    public MapCodec<? extends MarkData> codec() {
        return CODEC;
    }
}
