package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.AdvancedDominionWand;
import com.github.jarva.arsadditions.common.item.data.AdvancedDominionData;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public record PacketMultiTargetConnection(List<BlockPos> blockPositions, List<Integer> entityIds, InteractionHand hand) implements CustomPacketPayload {
    private static final int MAX_TARGETS = 1000; // Safety limit to prevent server lag
    private static final double MAX_DISTANCE = 128.0; // Maximum distance from player

    public static final CustomPacketPayload.Type<PacketMultiTargetConnection> TYPE =
            new CustomPacketPayload.Type<>(ArsAdditions.prefix("multi_target_connection"));

    private static final StreamCodec<ByteBuf, InteractionHand> HAND_CODEC = ByteBufCodecs.idMapper(
            i -> InteractionHand.values()[i],
            InteractionHand::ordinal
    );

    public static final StreamCodec<ByteBuf, PacketMultiTargetConnection> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    PacketMultiTargetConnection::blockPositions,
                    ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
                    PacketMultiTargetConnection::entityIds,
                    HAND_CODEC,
                    PacketMultiTargetConnection::hand,
                    PacketMultiTargetConnection::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            // Security: Validate list sizes to prevent server lag
            int totalTargets = blockPositions.size() + entityIds.size();
            if (totalTargets > MAX_TARGETS) {
                return;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof AdvancedDominionWand)) {
                return;
            }

            AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);
            if (data.level().isEmpty()) {
                return;
            }

            MinecraftServer server = serverLevel.getServer();
            ServerLevel originLevel = server.getLevel(data.level().get());
            if (originLevel == null) {
                return;
            }

            Triple<IWandable, LivingEntity, BlockPos> stored = getWandable(originLevel, data.pos(), data.entityId());
            IWandable storedWandable = stored.getLeft();
            LivingEntity storedLivingEntity = stored.getMiddle();
            BlockPos storedBlock = stored.getRight();

            int successCount = 0;
            int wandableCount = 0;

            for (BlockPos targetPos : blockPositions) {
                // Security: Validate distance from player
                if (player.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5) > MAX_DISTANCE * MAX_DISTANCE) {
                    continue;
                }

                // Security: Validate player has permission to interact with this block
                if (!BlockUtil.destroyRespectsClaim(player, serverLevel, targetPos)) {
                    continue;
                }

                BlockEntity be = serverLevel.getBlockEntity(targetPos);
                IWandable targetWandable = be instanceof IWandable wand ? wand : null;

                // At least one needs to be IWandable
                if (storedWandable == null && targetWandable == null) {
                    continue;
                }

                wandableCount++;
                if (performConnection(data, storedWandable, storedLivingEntity, storedBlock, targetWandable, null, targetPos, player)) {
                    successCount++;
                }
            }

            for (Integer entityId : entityIds) {
                LivingEntity targetEntity = serverLevel.getEntity(entityId) instanceof LivingEntity living ? living : null;
                if (targetEntity == null) {
                    continue;
                }

                // Security: Validate distance from player
                if (player.distanceToSqr(targetEntity) > MAX_DISTANCE * MAX_DISTANCE) {
                    continue;
                }

                // Security: Validate player has permission to interact with this entity's position
                if (!BlockUtil.destroyRespectsClaim(player, serverLevel, targetEntity.blockPosition())) {
                    continue;
                }

                IWandable targetWandable = targetEntity instanceof IWandable wand ? wand : null;

                // At least one needs to be IWandable
                if (storedWandable == null && targetWandable == null) {
                    continue;
                }

                wandableCount++;
                if (performConnection(data, storedWandable, storedLivingEntity, storedBlock, targetWandable, targetEntity, null, player)) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                PortUtil.sendMessageNoSpam(player,
                        Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_success", successCount, wandableCount));
            } else if (wandableCount > 0) {
                PortUtil.sendMessageNoSpam(player,
                        Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_failed", wandableCount));
            } else if (totalTargets > 0) {
                PortUtil.sendMessageNoSpam(player,
                        Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_not_wandable", totalTargets));
            } else {
                PortUtil.sendMessageNoSpam(player,
                        Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_no_targets"));
            }
        });
    }

    private boolean performConnection(AdvancedDominionData data, IWandable storedWandable, LivingEntity storedLivingEntity,
                                     BlockPos storedBlock, IWandable targetWandable, LivingEntity targetLivingEntity,
                                     BlockPos targetBlock, net.minecraft.world.entity.player.Player player) {
        switch (data.linkOrder()) {
            case FIRST -> {
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionLast(storedBlock, null, storedLivingEntity, player);
                }
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionFirst(targetBlock, null, targetLivingEntity, player);
                }
                return true;
            }
            case SECOND -> {
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionFirst(storedBlock, null, storedLivingEntity, player);
                }
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionLast(targetBlock, null, targetLivingEntity, player);
                }
                return true;
            }
        }
        return false;
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
}
