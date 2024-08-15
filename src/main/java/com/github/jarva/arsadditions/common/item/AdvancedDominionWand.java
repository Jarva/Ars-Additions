package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedDominionWand extends Item {
    public AdvancedDominionWand() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
        }

        if (player.isShiftKeyDown()) {
            if (!stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA)) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.fromEntity(serverLevel.dimension(), interactionTarget));
                PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
                return InteractionResult.SUCCESS;
            }

            AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA);
            IWandable wandable = interactionTarget instanceof IWandable wand ? wand : null;
            return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, interactionTarget, null));
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pPlayer.isShiftKeyDown()) {
            if (stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA)) {
                AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA);
                data.toggleMode();
                data.write(stack);
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("chat.ars_additions.advanced_dominion_wand.mode", data.mode.getTranslatable()));
                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
         if (!(context.getLevel() instanceof ServerLevel serverLevel) || context.getPlayer() == null) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();

        if (player.isShiftKeyDown()) {
            BlockPos pos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();

            BlockEntity be = serverLevel.getBlockEntity(pos);

            if (stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA)) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.fromPos(pos, serverLevel.dimension()));
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
                return InteractionResult.SUCCESS;
            }

            AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA);
            IWandable wandable = be instanceof IWandable wand ? wand : null;
            return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, null, pos));
        }

        return super.useOn(context);
    }

    private InteractionResult attemptConnection(MinecraftServer server, AdvancedDominionData data, Player player, Triple<IWandable, LivingEntity, BlockPos> target) {
        ServerLevel origin = server.getLevel(data.pos().dimension());

        IWandable targetWandable = target.getLeft();
        LivingEntity targetLivingEntity = target.getMiddle();
        BlockPos targetBlock = target.getRight();

        Triple<IWandable, LivingEntity, BlockPos> stored = getWandable(origin, data.pos().pos(), data.entityId());

        IWandable storedWandable = stored.getLeft();
        LivingEntity storedLivingEntity = stored.getMiddle();
        BlockPos storedBlock = stored.getRight();

        switch (data.mode()) {
            case LOCK_FIRST -> {
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionFirst(targetBlock, null, targetLivingEntity, player);
                }
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionLast(storedBlock, null, storedLivingEntity, player);
                }
            }
            case LOCK_SECOND -> {
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionLast(targetBlock, null, targetLivingEntity, player);
                }
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionFirst(storedBlock, null, storedLivingEntity, player);
                }
            }
            default -> {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }

    private Triple<IWandable, LivingEntity, BlockPos> getWandable(ServerLevel level, @Nullable BlockPos pos, @Nullable Integer entityId) {
        if (pos != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IWandable wandable) {
                return Triple.of(wandable, null, pos);
            }
            return Triple.of(null, null, pos);
        }
        if (entityId != null && level.getEntity(entityId) instanceof LivingEntity living) {
            if (living instanceof IWandable wandable) {
                return Triple.of(wandable, living, null);
            }
            return Triple.of(null, living, null);
        }
        return Triple.of(null, null, null);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
        if (!stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA))
            return;

        AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA);
        tooltip.add(Component.translatable("tooltip.ars_additions.advanced_dominion_wand.mode", data.mode().getTranslatable()));

        if (data.pos().pos() != null) {
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", data.pos().pos().getX(), data.pos().pos().getY(), data.pos().pos().getZ(), data.pos().dimension().location().toString()));
        } else {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        }
    }
}
