package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class DefaultLootProvider extends LootTableProvider {
    public DefaultLootProvider(PackOutput packOutput) {
        super(packOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class BlockLootTableProvider extends BlockLootSubProvider {
        public List<Block> list = new ArrayList<>();
        protected BlockLootTableProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>());
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> p_249322_) {
            this.generate();
            Set<ResourceLocation> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceLocation resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        p_249322_.accept(resourcelocation, loottable$builder);
                    }
                }
            }
        }

        @Override
        protected void generate() {
            this.list.add(AddonBlockRegistry.ENDER_SOURCE_JAR.get());
            this.add(AddonBlockRegistry.ENDER_SOURCE_JAR.get(), createManaMachineTable(AddonBlockRegistry.ENDER_SOURCE_JAR.get(), AddonBlockRegistry.ENDER_SOURCE_JAR_TILE.get()));
        }

        public LootTable.Builder createManaMachineTable(Block block, BlockEntityType<?> tile) {
            LootPool.Builder builder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("source", "BlockEntityTag.source", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("max_source", "BlockEntityTag.max_source", CopyNbtFunction.MergeStrategy.REPLACE)
                            )
                    );
            return LootTable.lootTable().withPool(builder);
        }
    }
}
