package com.github.jarva.arsadditions.common.item.data;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record ExplorationScrollData(Optional<HolderSet<Structure>> structure, Optional<Vec3> pos, int searchRadius, boolean skipKnownStructures) {
    public static final TagKey<Structure> DEFAULT_DESTINATION = TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix("on_explorer_warp_scroll"));
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;

    public static ExplorationScrollData fromItemStack(ItemStack stack) {
        return stack.getOrDefault(AddonDataComponentRegistry.EXPLORATION_SCROLL_DATA, new ExplorationScrollData(Optional.empty(), Optional.empty(), DEFAULT_SEARCH_RADIUS, DEFAULT_SKIP_EXISTING));
    }

    public static final Codec<ExplorationScrollData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structure").forGetter(ExplorationScrollData::structure),
            Vec3.CODEC.optionalFieldOf("pos").forGetter(ExplorationScrollData::pos),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("search_radius", 50).forGetter(ExplorationScrollData::searchRadius),
            Codec.BOOL.optionalFieldOf("skip_existing_chunks", true).forGetter(ExplorationScrollData::skipKnownStructures)
    ).apply(inst, ExplorationScrollData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExplorationScrollData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.STRUCTURE)), ExplorationScrollData::structure,
            ByteBufCodecs.optional(ByteBufCodecs.fromCodec(Vec3.CODEC)), ExplorationScrollData::pos,
            ByteBufCodecs.INT, ExplorationScrollData::searchRadius,
            ByteBufCodecs.BOOL, ExplorationScrollData::skipKnownStructures,
            ExplorationScrollData::new
    );
}
