package com.github.jarva.arsadditions.common.data;

import com.github.jarva.arsadditions.setup.networking.SendLocalWeatherStatus;
import com.github.jarva.arsadditions.setup.registry.AddonAttachmentRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.IntFunction;

@EventBusSubscriber
public enum WeatherStatus implements StringRepresentable {
    NONE(0),
    CLEAR(1),
    RAIN(2),
    SNOW(3),
    THUNDER(4);

    public static StringRepresentable.EnumCodec<WeatherStatus> CODEC = StringRepresentable.fromEnum(WeatherStatus::values);
    public static IntFunction<WeatherStatus> BY_ID = ByIdMap.continuous(WeatherStatus::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static StreamCodec<ByteBuf, WeatherStatus> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WeatherStatus::getId);

    private final int id;

    WeatherStatus(int i) {
        this.id = i;
    }

    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

    @SubscribeEvent
    public static void sendChunkData(ChunkWatchEvent.Sent event) {
        ServerPlayer player = event.getPlayer();
        LevelChunk chunk = event.getChunk();
        ChunkPos pos = event.getPos();

        if (chunk.hasData(AddonAttachmentRegistry.LOCAL_WEATHER)) {
            WeatherStatus status = chunk.getData(AddonAttachmentRegistry.LOCAL_WEATHER);
            SendLocalWeatherStatus.sendChunkStatus(player, pos, status);
        } else {
            SendLocalWeatherStatus.sendChunkStatus(player, pos, WeatherStatus.NONE);
        }
    }

    @SubscribeEvent
    public static void setWeatherStatus(ChunkWatchEvent.Watch event) {
        if (FMLEnvironment.production) return;

        ServerPlayer player = event.getPlayer();
        LevelChunk chunk = event.getChunk();
        ChunkPos pos = event.getPos();

        WeatherStatus[] values = WeatherStatus.values();

        if (!chunk.hasData(AddonAttachmentRegistry.LOCAL_WEATHER)) {
            int rnd = new Random().nextInt(values.length);
            WeatherStatus status = values[rnd];
            chunk.setData(AddonAttachmentRegistry.LOCAL_WEATHER, status);
        }
    }

    public static WeatherStatus getByPlayer(Player player) {
        ChunkPos pos = player.chunkPosition();
        Level level = player.level();
        return getByChunkPos(level, pos);
    }

    public static WeatherStatus getByBlockPos(LevelReader level, BlockPos blockPos) {
        ChunkPos pos = new ChunkPos(blockPos);
        return getByChunkPos(level, pos);
    }

    public static WeatherStatus getByChunkPos(LevelReader level, ChunkPos pos) {
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);
        return chunk.getExistingData(AddonAttachmentRegistry.LOCAL_WEATHER).orElse(WeatherStatus.NONE);
    }

    public static float getRainLevel(WeatherStatus status) {
        return switch (status) {
            case RAIN, SNOW, THUNDER -> 1.0f;
            default -> 0;
        };
    }

    public static boolean isRaining(WeatherStatus status) {
        return getRainLevel(status) > 0;
    }

    public static Biome.Precipitation getPrecipitation(WeatherStatus status) {
        return switch (status) {
            case RAIN, THUNDER -> Biome.Precipitation.RAIN;
            case SNOW -> Biome.Precipitation.SNOW;
            default -> Biome.Precipitation.NONE;
        };
    }
}
