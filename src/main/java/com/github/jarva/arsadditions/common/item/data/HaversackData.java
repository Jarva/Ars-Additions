package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record HaversackData(BlockPos pos, ResourceKey<Level> level, Boolean active, ArrayList<ItemStack> items, Boolean loaded) {
    public static final Codec<HaversackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("BindPos").forGetter(HaversackData::pos),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("BindDim").forGetter(HaversackData::level),
            Codec.BOOL.optionalFieldOf("Active", false).forGetter(HaversackData::active),
            ItemStack.CODEC.listOf().optionalFieldOf("Items", List.of()).xmap(Lists::newArrayList, list -> list).forGetter(HaversackData::items),
            Codec.BOOL.optionalFieldOf("Loaded", false).forGetter(HaversackData::loaded)
    ).apply(instance, HaversackData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HaversackData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, HaversackData::pos,
            ResourceKey.streamCodec(Registries.DIMENSION), HaversackData::level,
            ByteBufCodecs.BOOL, HaversackData::active,
            ItemStack.LIST_STREAM_CODEC.map(Lists::newArrayList, list -> list), HaversackData::items,
            ByteBufCodecs.BOOL, HaversackData::loaded,
            HaversackData::new
    );

    public static Optional<HaversackData> fromItemStack(ItemStack stack) {
        return Optional.ofNullable(stack.get(AddonDataComponentRegistry.HAVERSACK_DATA));
    }

    public HaversackData toggle() {
        return new HaversackData(pos, level, !active, items, loaded);
    }

    public void toggleLoaded() {
        new HaversackData(pos, level, active, items, !loaded);
    }

    public boolean add(ItemStack stack) {
        return items.add(stack.copy());
    }

    public boolean remove(ItemStack stack) {
        return items.removeIf(s -> ItemStack.isSameItem(s, stack));
    }

    public boolean containsStack(ItemStack stack) {
        return items.stream().anyMatch(s -> ItemStack.isSameItem(s, stack));
    }

    @Nullable
    public FilterableItemHandler getItemHandler(Level level) {
        if (!level.isLoaded(pos)) return null;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return null;

        return new FilterableItemHandler(level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null), InvUtil.filtersOnTile(be));
    }
}