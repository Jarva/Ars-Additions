package com.github.jarva.arsadditions.mixin.weather;


import com.github.jarva.arsadditions.common.data.WeatherStatus;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

public class WeatherMixins {
    @Mixin(ServerLevel.class)
    public abstract static class ServerLevelMixin extends Level {
        protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
            super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        }

//        @WrapOperation(method = "tickChunk",
//                at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
//        )
//        public int tickChunk$isThundering(RandomSource instance, int i, Operation<Integer> original) {
//            if (i == 100000) {
//                return original.call(instance, 10);
//            }
//            return original.call(instance, i);
//        }

        @WrapOperation(method = "tickChunk",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
        )
        public boolean tickChunk$isThundering(ServerLevel instance, Operation<Boolean> original, @Local ChunkPos chunkPos) {
            WeatherStatus status = WeatherStatus.getByChunkPos(this, chunkPos);
            if (status != WeatherStatus.NONE) {
                return status == WeatherStatus.THUNDER;
            }
            return original.call(instance);
        }

        @WrapOperation(method = "tickChunk",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
        )
        public boolean tickChunk$isRaining(ServerLevel instance, Operation<Boolean> original, @Local ChunkPos chunkPos) {
            WeatherStatus status = WeatherStatus.getByChunkPos(this, chunkPos);
            if (status != WeatherStatus.NONE) {
                return WeatherStatus.isRaining(status);
            }
            return original.call(instance);
        }

        @WrapOperation(method = "tickPrecipitation",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
        )
        public boolean tickPrecipitation$isRaining(ServerLevel instance, Operation<Boolean> original, @Local(argsOnly = true) BlockPos blockPos) {
            WeatherStatus status = WeatherStatus.getByBlockPos(this, blockPos);
            if (status != WeatherStatus.NONE) {
                return WeatherStatus.isRaining(status);
            }
            return original.call(instance);
        }

        @WrapOperation(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
        public Biome.Precipitation tickPrecipitation$getPrecipitationAt(Biome instance, BlockPos pPos, Operation<Biome.Precipitation> original) {
            WeatherStatus status = WeatherStatus.getByBlockPos(this, pPos);

            if (status != WeatherStatus.NONE) {
                return WeatherStatus.getPrecipitation(status);
            }

            if (this.isRaining()) {
                return original.call(instance, pPos);
            }
            return Biome.Precipitation.NONE;
        }
    }

    @Mixin(Level.class)
    public static abstract class LevelMixin {
        @Shadow public abstract boolean setBlock(BlockPos pos, BlockState newState, int flags);

        private Level getLevel() {
            return (Level) (Object) this;
        }

        @Inject(method = "isRainingAt", at = @At(value = "HEAD"), cancellable = true)
        public void isRainingAt(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            WeatherStatus status = WeatherStatus.getByBlockPos(getLevel(), pos);
            if (status == WeatherStatus.NONE) return;

            cir.setReturnValue(WeatherStatus.isRaining(status));
        }
    }

    @Mixin(ClientLevel.class)
    public abstract static class ClientLevelMixin extends Level {
        protected ClientLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
            super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        }

        @Override
        public float getRainLevel(float pDelta) {
            Player player = ArsNouveau.proxy.getPlayer();
            if (player == null) return super.getRainLevel(pDelta);

            WeatherStatus status = WeatherStatus.getByPlayer(player);
            if (status == WeatherStatus.NONE) {
                return super.getRainLevel(pDelta);
            }
            return WeatherStatus.getRainLevel(status);
        }
    }

    @Mixin(ClientLevel.ClientLevelData.class)
    public static class ClientLevelDataMixin {
        @Inject(method = "isRaining", at = @At(value = "HEAD"), cancellable = true)
        public void isRaining(CallbackInfoReturnable<Boolean> cir) {
            Player player = ArsNouveau.proxy.getPlayer();
            if (player == null) return;

            WeatherStatus status = WeatherStatus.getByPlayer(player);
            if (status != WeatherStatus.NONE) {
                cir.setReturnValue(WeatherStatus.isRaining(status));
            }
        }
    }

    @Mixin(Biome.class)
    public static class BiomeMixin {
        @WrapOperation(method = "shouldSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
        public boolean shouldSnow$warmEnoughToRain(Biome instance, BlockPos pos, Operation<Boolean> original, @Local LevelReader level) {
            WeatherStatus status = WeatherStatus.getByBlockPos(level, pos);
            if (status != WeatherStatus.NONE) {
                return status != WeatherStatus.SNOW;
            }
            return original.call(instance, pos);
        }

        @WrapOperation(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"))
        public boolean shouldFreeze$warmEnoughToRain(Biome instance, BlockPos pos, Operation<Boolean> original, @Local LevelReader level) {
            WeatherStatus status = WeatherStatus.getByBlockPos(level, pos);
            if (status != WeatherStatus.NONE) {
                return status != WeatherStatus.SNOW;
            }
            return original.call(instance, pos);
        }
    }

    @Mixin(LevelRenderer.class)
    public static abstract class LevelRendererMixin {
        @WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
        public float renderSnowAndRain$getRainLevel(ClientLevel instance, float v, Operation<Float> original) {
            return 1.0f;
        }

        @WrapOperation(method = "renderSnowAndRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
        public Biome.Precipitation getPrecipitation(Biome instance, BlockPos pPos, Operation<Biome.Precipitation> original) {
            Level level = ArsNouveau.proxy.getClientWorld();

            WeatherStatus status = WeatherStatus.getByBlockPos(level, pPos);

            if (status != WeatherStatus.NONE) {
                return WeatherStatus.getPrecipitation(status);
            }

            if (level.isRaining()) {
                return original.call(instance, pPos);
            }
            return Biome.Precipitation.NONE;
        }

        @WrapOperation(method = "tickRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
        public void addParticle(ClientLevel instance, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Operation<Void> original) {
            Level level = ArsNouveau.proxy.getClientWorld();

            WeatherStatus status = WeatherStatus.getByBlockPos(level, BlockPos.containing(pX, pY, pZ));

            if (status != WeatherStatus.CLEAR) {
                original.call(instance, pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            }
        }

        @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
        public float renderSky$getRainLevel(ClientLevel instance, float v, Operation<Float> original) {
            Player player = ArsNouveau.proxy.getPlayer();
            if (player == null) return original.call(instance, v);

            WeatherStatus status = WeatherStatus.getByPlayer(player);
            if (status == WeatherStatus.NONE) {
                return original.call(instance, v);
            }
            return WeatherStatus.getRainLevel(status);
        }
    }
}
