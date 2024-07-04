package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NexusWarpScroll extends StableWarpScroll {
    public NexusWarpScroll() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;

        if (level.getBlockState(context.getClickedPos()).is(AddonBlockRegistry.WARP_NEXUS.get())) {
            LocateUtil.setScrollData(serverLevel, context.getItemInHand(), context.getClickedPos());
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        WarpScroll.WarpScrollData data = new StableWarpScroll.StableScrollData(stack);
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
