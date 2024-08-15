package com.github.jarva.arsadditions.common.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AdvancedDominionData(GlobalPos pos, Mode mode, Integer entityId) {
    public static AdvancedDominionData fromPos(BlockPos pos, ResourceKey<Level> serverLevel) {
        return new AdvancedDominionData(new GlobalPos(serverLevel, pos), Mode.LOCK_FIRST, null);
    }

    public static AdvancedDominionData fromEntity(ResourceKey<Level> serverLevel, Entity entity) {
        return new AdvancedDominionData(new GlobalPos(serverLevel, null), Mode.LOCK_FIRST, entity.getId());
    }

    public enum Mode implements StringRepresentable {
        LOCK_FIRST("tooltip.ars_additions.advanced_dominion_wand.mode.first"),
        LOCK_SECOND("tooltip.ars_additions.advanced_dominion_wand.mode.second");

        private final String translatable;

        Mode(String translatable) {
            this.translatable = translatable;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }

        public Component getTranslatable() {
            return Component.translatable(translatable);
        }
    }

    public static AdvancedDominionData DEFAULT_DATA = new AdvancedDominionData(null, AdvancedDominionData.Mode.LOCK_FIRST, null);

    public static final Codec<AdvancedDominionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.optionalFieldOf("GlobalPos", null).forGetter(AdvancedDominionData::pos),
            StringRepresentable.fromEnum(AdvancedDominionData.Mode::values).optionalFieldOf("Mode", Mode.LOCK_FIRST).forGetter(AdvancedDominionData::mode),
            Codec.INT.optionalFieldOf("StoredEntity", null).forGetter(AdvancedDominionData::entityId)
    ).apply(instance, AdvancedDominionData::new));

    public static final StreamCodec<ByteBuf, AdvancedDominionData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public AdvancedDominionData toggleMode() {
        return new AdvancedDominionData(this.pos, this.mode == AdvancedDominionData.Mode.LOCK_FIRST ? AdvancedDominionData.Mode.LOCK_SECOND : AdvancedDominionData.Mode.LOCK_FIRST, this.entityId);
    }
}
