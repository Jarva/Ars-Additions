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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AdvancedDominionData(Optional<BlockPos> pos, Optional<ResourceKey<Level>> level, Optional<Integer> entityId, LinkOrder linkOrder, LinkCount linkCount) {
    public static AdvancedDominionData fromPos(BlockPos pos, ResourceKey<Level> serverLevel, LinkOrder linkOrder, LinkCount linkCount) {
        return new AdvancedDominionData(Optional.of(pos), Optional.of(serverLevel), Optional.empty(), linkOrder, linkCount);
    }

    public static AdvancedDominionData fromEntity(ResourceKey<Level> serverLevel, Entity entity, LinkOrder linkOrder, LinkCount linkCount) {
        return new AdvancedDominionData(Optional.empty(), Optional.of(serverLevel), Optional.of(entity.getId()), linkOrder, linkCount);
    }

    public enum LinkOrder implements StringRepresentable {
        FIRST("tooltip.ars_additions.advanced_dominion_wand.order.first"),
        SECOND("tooltip.ars_additions.advanced_dominion_wand.order.second");

        private final String translatable;

        LinkOrder(String translatable) {
            this.translatable = translatable;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }

        public Component getTranslatable() {
            return Component.translatable(translatable);
        }

        public LinkOrder toggle() {
            return this == FIRST ? SECOND : FIRST;
        }
    }

    public enum LinkCount implements StringRepresentable {
        SINGLE("tooltip.ars_additions.advanced_dominion_wand.count.single"),
        MULTI("tooltip.ars_additions.advanced_dominion_wand.count.multi");

        private final String translatable;

        LinkCount(String translatable) {
            this.translatable = translatable;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }

        public Component getTranslatable() {
            return Component.translatable(translatable);
        }

        public LinkCount toggle() {
            return this == SINGLE ? MULTI : SINGLE;
        }
    }

    @NotNull
    public static AdvancedDominionData fromItemStack(ItemStack stack) {
        return stack.getOrDefault(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA.get(), new AdvancedDominionData(Optional.empty(), Optional.empty(), Optional.empty(), LinkOrder.FIRST, LinkCount.SINGLE));
    }

    public static final Codec<AdvancedDominionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("Pos").forGetter(AdvancedDominionData::pos),
            Level.RESOURCE_KEY_CODEC.optionalFieldOf("Level").forGetter(AdvancedDominionData::level),
            Codec.INT.optionalFieldOf("StoredEntity").forGetter(AdvancedDominionData::entityId),
            StringRepresentable.fromEnum(LinkOrder::values).optionalFieldOf("LinkOrder", LinkOrder.FIRST).forGetter(AdvancedDominionData::linkOrder),
            StringRepresentable.fromEnum(LinkCount::values).optionalFieldOf("LinkCount", LinkCount.SINGLE).forGetter(AdvancedDominionData::linkCount)
    ).apply(instance, AdvancedDominionData::new));

    public static final StreamCodec<ByteBuf, AdvancedDominionData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public AdvancedDominionData toggleLinkOrder() {
        return new AdvancedDominionData(this.pos, this.level, this.entityId, this.linkOrder.toggle(), this.linkCount);
    }

    public AdvancedDominionData toggleLinkCount() {
        return new AdvancedDominionData(this.pos, this.level, this.entityId, this.linkOrder, this.linkCount.toggle());
    }

    public AdvancedDominionData clear() {
        return new AdvancedDominionData(Optional.empty(), Optional.empty(), Optional.empty(), this.linkOrder, this.linkCount);
    }

    /**
     * @param stack The ItemStack to write the component to
     * @return Returns the previously stored value of component or null if previously unset
     */
    @Nullable
    public AdvancedDominionData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.ADVANCED_DOMINION_DATA, this);
    }
}
