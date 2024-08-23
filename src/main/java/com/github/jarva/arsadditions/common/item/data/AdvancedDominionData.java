package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AdvancedDominionData(Optional<BlockPos> pos, Optional<ResourceKey<Level>> level, Optional<Integer> entityId, Mode mode) {
    public static AdvancedDominionData fromPos(BlockPos pos, ResourceKey<Level> serverLevel) {
        return new AdvancedDominionData(Optional.of(pos), Optional.of(serverLevel), Optional.empty(), Mode.LOCK_FIRST);
    }

    public static AdvancedDominionData fromEntity(ResourceKey<Level> serverLevel, Entity entity) {
        return new AdvancedDominionData(Optional.empty(), Optional.of(serverLevel), Optional.of(entity.getId()), Mode.LOCK_FIRST);
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

    public static AdvancedDominionData fromItemStack(ItemStack stack) {
        return stack.getOrDefault(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), new AdvancedDominionData(Optional.empty(), Optional.empty(), Optional.empty(), AdvancedDominionData.Mode.LOCK_FIRST));
    }

    public static final Codec<AdvancedDominionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("Pos").forGetter(AdvancedDominionData::pos),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("Level").forGetter(AdvancedDominionData::level),
            Codec.INT.optionalFieldOf("StoredEntity").forGetter(AdvancedDominionData::entityId),
            StringRepresentable.fromEnum(AdvancedDominionData.Mode::values).optionalFieldOf("Mode", Mode.LOCK_FIRST).forGetter(AdvancedDominionData::mode)
    ).apply(instance, AdvancedDominionData::new));

    public static final StreamCodec<ByteBuf, AdvancedDominionData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public AdvancedDominionData toggleMode() {
        return new AdvancedDominionData(this.pos, this.level, this.entityId, this.mode == AdvancedDominionData.Mode.LOCK_FIRST ? AdvancedDominionData.Mode.LOCK_SECOND : AdvancedDominionData.Mode.LOCK_FIRST);
    }

    public AdvancedDominionData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, this);
    }
}
