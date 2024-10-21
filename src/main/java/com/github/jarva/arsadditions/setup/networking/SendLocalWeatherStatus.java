package com.github.jarva.arsadditions.setup.networking;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.data.WeatherStatus;
import com.github.jarva.arsadditions.setup.registry.AddonAttachmentRegistry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendLocalWeatherStatus(ChunkPos pos, WeatherStatus status) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendLocalWeatherStatus> TYPE = new CustomPacketPayload.Type<>(ArsAdditions.prefix("flag_chunk_local_weather"));

    public static final StreamCodec<FriendlyByteBuf, SendLocalWeatherStatus> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.CHUNK_POS, SendLocalWeatherStatus::pos,
            WeatherStatus.STREAM_CODEC, SendLocalWeatherStatus::status,
            SendLocalWeatherStatus::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleData(SendLocalWeatherStatus packet, IPayloadContext context) {
        Player player = ArsNouveau.proxy.getPlayer();
        if (player == null) return;

        ChunkPos pos = packet.pos;

        LevelChunk chunk = player.level().getChunk(pos.x, pos.z);
        if (chunk == null) return;

        if (packet.status != WeatherStatus.NONE) {
            ArsAdditions.LOGGER.info("Received status {} for chunk {}, {}", packet.status, pos.x, pos.z);
        }

        if (packet.status == WeatherStatus.NONE) {
            chunk.removeData(AddonAttachmentRegistry.LOCAL_WEATHER);
            return;
        }

        chunk.setData(AddonAttachmentRegistry.LOCAL_WEATHER, packet.status);
    }

    public static void sendChunkStatus(ServerPlayer player, ChunkPos chunk, WeatherStatus status) {
        NetworkHandler.sendToPlayerClient(new SendLocalWeatherStatus(chunk, status), player);
    }
}
