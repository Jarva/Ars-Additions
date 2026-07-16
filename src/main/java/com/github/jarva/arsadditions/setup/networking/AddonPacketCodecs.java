package com.github.jarva.arsadditions.setup.networking;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

public final class AddonPacketCodecs {
    private static final InteractionHand[] HAND_VALUES = InteractionHand.values();

    public static final StreamCodec<ByteBuf, InteractionHand> INTERACTION_HAND = ByteBufCodecs.idMapper(
            AddonPacketCodecs::decodeHand,
            InteractionHand::ordinal
    );

    private AddonPacketCodecs() {
    }

    private static InteractionHand decodeHand(int ordinal) {
        if (ordinal < 0 || ordinal >= HAND_VALUES.length) {
            throw new DecoderException("Invalid InteractionHand ordinal: " + ordinal);
        }
        return HAND_VALUES[ordinal];
    }
}
