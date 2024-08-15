package com.github.jarva.arsadditions.common.loot.functions;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.ExplorationScrollData;
import com.github.jarva.arsadditions.common.util.codec.ResourceOrTag;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonLootItemFunctionsRegistry;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ExplorationScrollFunction extends LootItemConditionalFunction {
    private final Optional<HolderSet<Structure>> destination;
    private final int searchRadius;
    private final boolean skipKnownStructures;

    public static final MapCodec<ExplorationScrollFunction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(func -> func.predicates),
            RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structure").forGetter(func -> func.destination),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("search_radius", ExplorationScrollData.DEFAULT_SEARCH_RADIUS).forGetter(func -> func.searchRadius),
            Codec.BOOL.optionalFieldOf("skip_existing_chunks", ExplorationScrollData.DEFAULT_SKIP_EXISTING).forGetter(func -> func.skipKnownStructures)
    ).apply(inst, ExplorationScrollFunction::new));

    protected ExplorationScrollFunction(List<LootItemCondition> predicates, Optional<HolderSet<Structure>> destination, int searchRadius, boolean skipKnownStructures) {
        super(predicates);
        this.destination = destination;
        this.searchRadius = searchRadius;
        this.skipKnownStructures = skipKnownStructures;
    }

    @Override
    public LootItemFunctionType<ExplorationScrollFunction> getType() {
        return AddonLootItemFunctionsRegistry.EXPLORATION_SCROLL_TYPE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!stack.is(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get())) return stack;

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);

        saveData(stack, origin);

        if (origin == null) return stack;

        ServerLevel level = context.getLevel();

        LocateUtil.locateWithState(stack, level, destination.orElse(context.getLevel().registryAccess().registry(Registries.STRUCTURE).orElseThrow().getOrCreateTag(ExplorationScrollData.DEFAULT_DESTINATION)), BlockPos.containing(origin), searchRadius, skipKnownStructures);
        return stack;
    }

    private void saveData(ItemStack stack, Vec3 origin) {
        stack.set(AddonDataComponentRegistry.EXPLORATION_SCROLL_DATA, new ExplorationScrollData(destination, Optional.ofNullable(origin), searchRadius, skipKnownStructures));
    }
}
