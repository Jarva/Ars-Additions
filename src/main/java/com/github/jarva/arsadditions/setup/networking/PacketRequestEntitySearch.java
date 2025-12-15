package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.client.util.MultiTargetUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketRequestEntitySearch(int targetEntityId, InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketRequestEntitySearch> TYPE =
            new CustomPacketPayload.Type<>(ArsAdditions.prefix("request_entity_search"));

    private static final StreamCodec<ByteBuf, InteractionHand> HAND_CODEC = ByteBufCodecs.idMapper(
            i -> InteractionHand.values()[i],
            InteractionHand::ordinal
    );

    public static final StreamCodec<ByteBuf, PacketRequestEntitySearch> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    PacketRequestEntitySearch::targetEntityId,
                    HAND_CODEC,
                    PacketRequestEntitySearch::hand,
                    PacketRequestEntitySearch::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleData(IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!player.level().isClientSide) {
                return;
            }

            LivingEntity targetEntity = player.level().getEntity(targetEntityId) instanceof LivingEntity living ? living : null;
            if (targetEntity == null) {
                PortUtil.sendMessageNoSpam(player, Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_no_targets"));
                return;
            }

            List<LivingEntity> nearbyEntities = MultiTargetUtil.findNearbyEntities(player.level(), targetEntity);
            List<Integer> entityIds = nearbyEntities.stream()
                    .map(LivingEntity::getId)
                    .toList();

            if (!entityIds.isEmpty()) {
                NetworkHandler.sendToServer(new PacketMultiTargetConnection(List.of(), entityIds, hand));
            } else {
                PortUtil.sendMessageNoSpam(player, Component.translatable("chat.ars_additions.advanced_dominion_wand.multi_link_no_targets"));
            }
        });
    }
}
