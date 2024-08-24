package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.WayfinderData;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.LodestoneTracker;

import java.util.List;

public class Wayfinder extends Item {
    public Wayfinder() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (stack.has(AddonDataComponentRegistry.WAYFINDER_DATA)) {
            WayfinderData data = stack.get(AddonDataComponentRegistry.WAYFINDER_DATA);
            tooltipComponents.add(data.text());
        }
        if (stack.has(DataComponents.LODESTONE_TRACKER)) {
            LodestoneTracker lodestoneTracker = stack.get(DataComponents.LODESTONE_TRACKER);
            lodestoneTracker.target().ifPresent(global -> {
                Player player = ArsNouveau.proxy.getPlayer();
                if (!global.dimension().equals(player.level().dimension())) return;
                int distance = global.pos().distManhattan(player.blockPosition());
                tooltipComponents.add(Component.translatable("tooltip.ars_additions.wayfinder.distance", distance));
            });
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (stack.has(AddonDataComponentRegistry.WAYFINDER_DATA)) {
            return Util.makeDescriptionId("item", ArsAdditions.prefix("bound_wayfinder"));
        }
        return super.getDescriptionId(stack);
    }
}
