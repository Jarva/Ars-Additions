package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record HaversackData(GlobalPos pos, Direction side, Boolean active, List<ItemStack> items, Boolean loaded) {
    public static final Codec<HaversackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GlobalPos.CODEC.fieldOf("bind_pos").forGetter(HaversackData::pos),
            Direction.CODEC.optionalFieldOf("bind_side", Direction.UP).forGetter(HaversackData::side),
            Codec.BOOL.optionalFieldOf("active", false).forGetter(HaversackData::active),
            ItemStack.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(HaversackData::items),
            Codec.BOOL.optionalFieldOf("Loaded", false).forGetter(HaversackData::loaded)
    ).apply(instance, HaversackData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HaversackData> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, HaversackData::pos,
            Direction.STREAM_CODEC, HaversackData::side,
            ByteBufCodecs.BOOL, HaversackData::active,
            ItemStack.LIST_STREAM_CODEC, HaversackData::items,
            ByteBufCodecs.BOOL, HaversackData::loaded,
            HaversackData::new
    );

    public static Optional<HaversackData> fromItemStack(ItemStack stack) {
        return Optional.ofNullable(stack.get(AddonDataComponentRegistry.HAVERSACK_DATA));
    }

    public HaversackData toggle() {
        return new HaversackData(pos, side, !active, items, loaded);
    }

    public HaversackData toggleLoaded() {
        return new HaversackData(pos, side, active, items, !loaded);
    }

    public HaversackData add(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<>(items);
        list.add(stack.copy());
        return new HaversackData(pos, side, active, list, loaded);
    }

    public HaversackData remove(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<>(items);
        if (list.removeIf(s -> ItemStack.isSameItem(s, stack))) {
            return new HaversackData(pos, side, active, list, loaded);
        }
        return this;
    }

    public HaversackData write(ItemStack stack) {
        return stack.set(AddonDataComponentRegistry.HAVERSACK_DATA, this);
    }

    public boolean containsStack(ItemStack stack) {
        return items.stream().anyMatch(s -> ItemStack.isSameItem(s, stack));
    }

    @Nullable
    public FilterableItemHandler getItemHandler(Level level) {
        if (!level.isLoaded(pos.pos())) return null;

        BlockEntity be = level.getBlockEntity(pos.pos());
        if (be == null) return null;

        return new FilterableItemHandler(level.getCapability(Capabilities.ItemHandler.BLOCK, pos.pos(), side), InvUtil.filtersOnTile(be));
    }
}
