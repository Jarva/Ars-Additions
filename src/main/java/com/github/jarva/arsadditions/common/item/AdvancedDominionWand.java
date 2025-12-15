package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.client.util.MultiTargetUtil;
import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.networking.NetworkHandler;
import com.github.jarva.arsadditions.setup.networking.PacketMultiTargetConnection;
import com.github.jarva.arsadditions.setup.networking.PacketRequestEntitySearch;
import com.github.jarva.arsadditions.setup.networking.PacketUpdateAdvancedDominionWand;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdvancedDominionWand extends Item implements IRadialProvider {
    public AdvancedDominionWand() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    public enum AdvancedDominionSlots {
        CLEAR("tooltip.ars_additions.advanced_dominion_wand.radial.clear"),
        TOGGLE_ORDER("tooltip.ars_additions.advanced_dominion_wand.radial.toggle"),
        TOGGLE_COUNT("tooltip.ars_additions.advanced_dominion_wand.radial.toggle");

        public final String key;

        AdvancedDominionSlots(String key) {
            this.key = key;
        }

        public Component translatable(AdvancedDominionData data) {
            return switch (this) {
                case CLEAR -> Component.translatable(key);
                case TOGGLE_ORDER -> Component.translatable(key,
                        data.linkOrder().getTranslatable().getString(),
                        data.linkOrder().toggle().getTranslatable().getString());
                case TOGGLE_COUNT -> Component.translatable(key,
                        data.linkCount().getTranslatable().getString(),
                        data.linkCount().toggle().getTranslatable().getString());
            };
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProvider(stack)));
    }

    public RadialMenu<String> getRadialMenuProvider(ItemStack stack) {
        AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);
        return new RadialMenu<>(
                (int slot) -> {
                    // Map display order to enum ordinal
                    AdvancedDominionSlots[] displayOrder = {
                        AdvancedDominionSlots.TOGGLE_ORDER,
                        AdvancedDominionSlots.CLEAR,
                        AdvancedDominionSlots.TOGGLE_COUNT
                    };
                    NetworkHandler.sendToServer(new PacketUpdateAdvancedDominionWand(displayOrder[slot].ordinal()));
                },
                getRadialMenuSlots(data),
                RenderUtils::drawString,
                0
        );
    }

    public List<RadialMenuSlot<String>> getRadialMenuSlots(AdvancedDominionData data) {
        List<RadialMenuSlot<String>> radialMenuSlots = new ArrayList<>();

        String toggleOrderText = AdvancedDominionSlots.TOGGLE_ORDER.translatable(data).getString();
        radialMenuSlots.add(new RadialMenuSlot<>(toggleOrderText,
                data.linkOrder().getTranslatable().getString()));

        String clearText = AdvancedDominionSlots.CLEAR.translatable(data).getString();
        radialMenuSlots.add(new RadialMenuSlot<>(clearText, "Clear"));

        String toggleCountText = AdvancedDominionSlots.TOGGLE_COUNT.translatable(data).getString();
        radialMenuSlots.add(new RadialMenuSlot<>(toggleCountText,
                data.linkCount().getTranslatable().getString()));

        return radialMenuSlots;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
        }

        if (player.isShiftKeyDown()) {
            if (data.pos().isEmpty() && data.entityId().isEmpty()) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), AdvancedDominionData.fromEntity(serverLevel.dimension(), interactionTarget, data.linkOrder(), data.linkCount()));
                PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
                return InteractionResult.SUCCESS;
            }

            if (data.level().isPresent() && data.linkCount() == AdvancedDominionData.LinkCount.MULTI) {
                NetworkHandler.sendToPlayerClient(new PacketRequestEntitySearch(interactionTarget.getId(), usedHand), (net.minecraft.server.level.ServerPlayer) player);
                return InteractionResult.SUCCESS;
            }

            if (data.level().isPresent() && data.linkCount() == AdvancedDominionData.LinkCount.SINGLE) {
                IWandable wandable = interactionTarget instanceof IWandable wand ? wand : null;
                InteractionResult result = attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, interactionTarget, null));
                if (result == InteractionResult.SUCCESS) {
                    PortUtil.sendMessageNoSpam(player, Component.translatable("chat.ars_additions.advanced_dominion_wand.link_success"));
                }
                return result;
            }
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);

        if (context.getLevel().isClientSide && player.isShiftKeyDown() && data.level().isPresent() &&
            data.linkCount() == AdvancedDominionData.LinkCount.MULTI) {

            List<BlockPos> connectedBlocks = MultiTargetUtil.findConnectedBlocks(context.getLevel(), pos);
            NetworkHandler.sendToServer(new PacketMultiTargetConnection(connectedBlocks, List.of(), context.getHand()));
            return InteractionResult.SUCCESS;
        }

        if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
            return super.useOn(context);
        }

        if (player.isShiftKeyDown()) {
            BlockEntity be = serverLevel.getBlockEntity(pos);

            if (data.pos().isEmpty() && data.entityId().isEmpty()) {
                stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), AdvancedDominionData.fromPos(pos, serverLevel.dimension(), data.linkOrder(), data.linkCount()));
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
                return InteractionResult.SUCCESS;
            }

            if (data.level().isPresent() && data.linkCount() == AdvancedDominionData.LinkCount.SINGLE) {
                IWandable wandable = be instanceof IWandable wand ? wand : null;
                InteractionResult result = attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, null, pos));
                if (result == InteractionResult.SUCCESS) {
                    PortUtil.sendMessageNoSpam(player, Component.translatable("chat.ars_additions.advanced_dominion_wand.link_success"));
                }
                return result;
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

        // At least one needs to be IWandable
        if (storedWandable == null && targetWandable == null) {
            return InteractionResult.FAIL;
        }

        // Determine which connection is first/last based on link order
        boolean storedIsFirst = data.linkOrder() == AdvancedDominionData.LinkOrder.FIRST;

        if (targetWandable != null) {
            if (storedIsFirst) {
                targetWandable.onFinishedConnectionLast(storedBlock, null, storedLivingEntity, player);
            } else {
                targetWandable.onFinishedConnectionFirst(storedBlock, null, storedLivingEntity, player);
            }
        }

        if (storedWandable != null) {
            if (storedIsFirst) {
                storedWandable.onFinishedConnectionFirst(targetBlock, null, targetLivingEntity, player);
            } else {
                storedWandable.onFinishedConnectionLast(targetBlock, null, targetLivingEntity, player);
            }
        }

        return InteractionResult.SUCCESS;
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
        if (!stack.has(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get()))
            return;

        AdvancedDominionData data = stack.get(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get());

        tooltip.add(Component.translatable("tooltip.ars_additions.advanced_dominion_wand.link_order",
                data.linkOrder().getTranslatable()));

        tooltip.add(Component.translatable("tooltip.ars_additions.advanced_dominion_wand.link_count",
                data.linkCount().getTranslatable()));

        if (data.pos().isPresent() && data.level().isPresent()) {
            BlockPos pos = data.pos().get();
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", pos.getX(), pos.getY(), pos.getZ(), data.level().get().location().toString()));
        } else {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        }
    }
}
