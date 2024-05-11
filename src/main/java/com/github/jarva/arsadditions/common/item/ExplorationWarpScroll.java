package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.client.util.KeypressUtil;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplorationWarpScroll extends Item {
    public ExplorationWarpScroll() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!(entity.getCommandSenderWorld() instanceof ServerLevel serverLevel))
            return false;

        if (LocateUtil.isPending(stack)) {
            LocateUtil.resolveUUID(serverLevel, entity.position(), stack, null);
        }

        BlockPos pos = entity.blockPosition();
        boolean isRuinedPortal = serverLevel.structureManager().getStructureWithPieceAt(pos, TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix("ruined_portals"))).isValid();
        if (isRuinedPortal) {
            WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
            if (!data.isValid()) return false;

            String displayName = "Explorer's Warp Portal";
            if (BlockRegistry.PORTAL_BLOCK.get().trySpawnPortal(serverLevel, pos, data, displayName)) {
                ANCriteriaTriggers.rewardNearbyPlayers(Triggers.FIND_RUINED_PORTAL, serverLevel, pos, 10);
                ANCriteriaTriggers.rewardNearbyPlayers(Triggers.CREATE_RUINED_PORTAL, serverLevel, pos, 10);
                TeleportUtil.createTeleportDecoration(serverLevel, pos, stack);
                return true;
            }
        }

        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (LocateUtil.isPending(stack)) {
            LocateUtil.resolveUUID(serverLevel, entity.position(), stack, entity);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("tooltip.ars_additions.exploration_warp_scroll.desc"));

        if (LocateUtil.isPending(stack)) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.exploration_warp_scroll.locating"));
        }

        if (KeypressUtil.isShiftPressed()) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.exploration_warp_scroll.use", Minecraft.getInstance().options.keyUse.getKey().getDisplayName()));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (usedHand == InteractionHand.OFF_HAND) return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if (!(level instanceof ServerLevel serverLevel)) return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        WarpScroll.WarpScrollData data = StableWarpScroll.StableScrollData.get(stack);

        if (LocateUtil.isPending(stack)) {
            PortUtil.sendMessageNoSpam(player, Component.translatable("tooltip.ars_additions.exploration_warp_scroll.locating"));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        if (!data.isValid()) {
            PortUtil.sendMessageNoSpam(player, Component.translatable("tooltip.ars_additions.exploration_warp_scroll.locating"));
            LocateUtil.locateFromStack(serverLevel, player.position(), stack);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else {
            TeleportUtil.teleport(serverLevel, data, player, stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}
