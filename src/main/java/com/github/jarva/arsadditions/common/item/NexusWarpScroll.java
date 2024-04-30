package com.github.jarva.arsadditions.common.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NexusWarpScroll extends StableWarpScroll {
    public NexusWarpScroll() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).tab(ArsNouveau.itemGroup));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
        if (data.isValid()) {
            BlockPos pos = data.getPos();
            tooltipComponents.add(Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
            String dimId = data.getDimension();
            if(dimId != null) {
                ResourceLocation resourceLocation = new ResourceLocation(dimId);
                tooltipComponents.add(Component.translatable(resourceLocation.getPath() + "." + resourceLocation.getNamespace() + ".name"));
            }
        }
    }
}
