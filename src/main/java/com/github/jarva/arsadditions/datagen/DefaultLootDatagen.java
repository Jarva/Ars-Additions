package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.common.block.EnchantingWixieCauldron;
import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyCustomDataFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DefaultLootDatagen extends LootTableProvider {
    public DefaultLootDatagen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)), registries);
    }

    public static class BlockLootTableProvider extends BlockLootSubProvider {
        private final List<Block> list = new ArrayList<>();
        protected BlockLootTableProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), new HashMap<>(), provider);
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
            this.generate();
            Set<ResourceKey<LootTable>> set = new HashSet<>();

            for (Block block : list) {
                if (block.isEnabled(this.enabledFeatures)) {
                    ResourceKey<LootTable> resourcelocation = block.getLootTable();
                    if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
                        LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
                        if (loottable$builder == null) {
                            continue;
                        }

                        output.accept(resourcelocation, loottable$builder);
                    }
                }
            }
        }

        @Override
        protected void generate() {
            registerManaMachine(AddonBlockRegistry.ENDER_SOURCE_JAR, AddonBlockRegistry.ENDER_SOURCE_JAR_TILE);

            String[][] nameList = new String[][]{
                    AddonBlockNames.CHAINS, AddonBlockNames.MAGELIGHT_LANTERNS,
                    AddonBlockNames.LANTERNS, AddonBlockNames.BUTTONS, AddonBlockNames.DECORATIVE_SOURCESTONES,
                    AddonBlockNames.WALLS, AddonBlockNames.TRAPDOORS,
                    AddonBlockNames.CARPETS
            };
            for (String[] names : nameList) {
                for (Block block : AddonBlockRegistry.getBlocks(names)) {
                    registerDropSelf(block);
                }
            }

            String[] doors = AddonBlockNames.DOORS;
            for (Block door : AddonBlockRegistry.getBlocks(doors)) {
                this.list.add(door);
                this.add(door, createDoorTable(door));
            }

            WarpNexus warpNexus = AddonBlockRegistry.WARP_NEXUS.get();
            LootPool.Builder nexusBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(warpNexus)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyCustomDataFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("Inventory", "BlockEntityTag.Inventory", CopyCustomDataFunction.MergeStrategy.REPLACE)
                                    .copy("color", "BlockEntityTag.color", CopyCustomDataFunction.MergeStrategy.REPLACE)
                            )
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(warpNexus).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WarpNexus.HALF, DoubleBlockHalf.LOWER)))
                    );
            this.list.add(warpNexus);
            this.add(warpNexus, LootTable.lootTable().withPool(nexusBuilder));

            EnchantingWixieCauldron enchantingWixieCauldron = AddonBlockRegistry.WIXIE_ENCHANTING.get();
            this.list.add(enchantingWixieCauldron);
            dropOther(enchantingWixieCauldron, BlockRegistry.ENCHANTING_APP_BLOCK);
        }

        private void registerDropSelf(Block block) {
            this.list.add(block);
            dropSelf(block);
        }

        private <B extends Block, T extends BlockEntity> void registerManaMachine(BlockRegistryWrapper<B> block, BlockEntityTypeRegistryWrapper<T> tile) {
            this.list.add(block.get());
            this.add(block.get(), createManaMachineTable(block.get(), tile.get()));
        }

        public LootTable.Builder createManaMachineTable(Block block, BlockEntityType<?> tile) {
            LootPool.Builder builder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyCustomDataFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("source", "BlockEntityTag.source", CopyCustomDataFunction.MergeStrategy.REPLACE)
                                    .copy("max_source", "BlockEntityTag.max_source", CopyCustomDataFunction.MergeStrategy.REPLACE)
                            )
                    );
            return LootTable.lootTable().withPool(builder);
        }
    }
}
