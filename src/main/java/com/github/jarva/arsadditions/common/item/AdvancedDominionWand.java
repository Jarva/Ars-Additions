package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
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
            AdvancedDominionData data = stack.getOrDefault(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.DEFAULT_DATA);
            if (data.pos().isEmpty() && data.entityId().isEmpty()) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.fromEntity(serverLevel.dimension(), interactionTarget));
                PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
                return InteractionResult.SUCCESS;
            }

            if (data.level().isPresent()) {
                IWandable wandable = interactionTarget instanceof IWandable wand ? wand : null;
                return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, interactionTarget, null));
            }
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pPlayer.isShiftKeyDown()) {
<<<<<<< HEAD
            if (stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA)) {
                AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA);
                data.toggleMode();
                data.write(stack);
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("chat.ars_additions.advanced_dominion_wand.mode", data.mode.getTranslatable()));
                return InteractionResultHolder.success(stack);
            }
=======
            AdvancedDominionData data = stack.getOrDefault(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.DEFAULT_DATA);
            data.toggleMode().write(stack);
            PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("chat.ars_additions.advanced_dominion_wand.mode", data.mode.getTranslatable()));
            return InteractionResultHolder.success(stack);
>>>>>>> b6c67b2 (fix: update datagen and fix bugs)
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

            AdvancedDominionData data = stack.getOrDefault(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.DEFAULT_DATA);
            if (data.pos().isEmpty() && data.entityId().isEmpty()) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, AdvancedDominionData.fromPos(pos, serverLevel.dimension()));
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
                return InteractionResult.SUCCESS;
            }

            if (data.level().isPresent()) {
                IWandable wandable = be instanceof IWandable wand ? wand : null;
                return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, null, pos));
            }
        }

        return super.useOn(context);
    }

    private InteractionResult attemptConnection(MinecraftServer server, AdvancedDominionData data, Player player, Triple<IWandable, LivingEntity, BlockPos> target) {
        ServerLevel origin = server.getLevel(data.level().get());

        IWandable targetWandable = target.getLeft();
        LivingEntity targetLivingEntity = target.getMiddle();
        BlockPos targetBlock = target.getRight();

        Triple<IWandable, LivingEntity, BlockPos> stored = getWandable(origin, data.pos(), data.entityId());

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

    private Triple<IWandable, LivingEntity, BlockPos> getWandable(ServerLevel level, Optional<BlockPos> pos, Optional<Integer> entityId) {
        if (pos.isPresent()) {
            BlockEntity be = level.getBlockEntity(pos.get());
            if (be instanceof IWandable wandable) {
                return Triple.of(wandable, null, pos.get());
            }
            return Triple.of(null, null, pos.get());
        }
        if (entityId.isPresent() && level.getEntity(entityId.get()) instanceof LivingEntity living) {
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

        if (data.pos().isPresent() && data.level().isPresent()) {
            BlockPos pos = data.pos().get();
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", pos.getX(), pos.getY(), pos.getZ(), data.level().get().location().toString()));
        } else {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        }
    }
}
