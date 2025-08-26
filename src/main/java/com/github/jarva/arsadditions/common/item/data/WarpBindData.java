package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record WarpBindData(GlobalPos pos) {
    public static final Codec<WarpBindData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(WarpBindData::pos)
    ).apply(instance, WarpBindData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpBindData> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, WarpBindData::pos, WarpBindData::new
    );

    public WarpBindData(Level level, BlockPos pos) {
        this(new GlobalPos(level.dimension(), pos));
    }

    public static Optional<WarpBindData> fromItemStack(ItemStack stack) {
        return Optional.ofNullable(stack.get(AddonDataComponentRegistry.WARP_BIND_DATA));
    }

    public boolean isIn(ResourceKey<Level> dimension) {
        return this.pos().dimension().equals(dimension);
    }

    public String getDimensionString() {
        return this.pos().dimension().location().toString();
    }

    public int x() {
        return this.pos().pos().getX();
    }

    public int y() {
        return this.pos().pos().getY();
    }

    public int z() {
        return this.pos().pos().getZ();
    }

    public BlockPos blockPos() {
        return this.pos().pos();
    }

    public ResourceKey<Level> dimension() {
        return this.pos().dimension();
    }

    public WarpBindData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.WARP_BIND_DATA, this);
    }
}
