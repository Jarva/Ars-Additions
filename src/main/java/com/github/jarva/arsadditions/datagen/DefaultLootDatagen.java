package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.block.WarpNexus;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.registry.names.AddonBlockNames;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

import static com.github.jarva.arsadditions.registry.AddonBlockRegistry.getBlock;

public class DefaultLootDatagen extends LootTableProvider {
    public DefaultLootDatagen(PackOutput packOutput) {
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
            registerManaMachine(AddonBlockRegistry.ENDER_SOURCE_JAR, AddonBlockRegistry.ENDER_SOURCE_JAR_TILE);
            for (String chain : AddonBlockNames.CHAINS) {
                registerDropSelf(getBlock(chain));
            }
            for (String magelightLantern : AddonBlockNames.MAGELIGHT_LANTERNS) {
                registerDropSelf(getBlock(magelightLantern));
            }
            for (String lantern : AddonBlockNames.LANTERNS) {
                registerDropSelf(getBlock(lantern));
            }
            for (String button : AddonBlockNames.BUTTONS) {
                registerDropSelf(getBlock(button));
            }
            for (String decorativeSourcestone : AddonBlockNames.DECORATIVE_SOURCESTONES) {
                registerDropSelf(getBlock(decorativeSourcestone));
            }
            for (String wall : AddonBlockNames.WALLS) {
                registerDropSelf(getBlock(wall));
            }

            WarpNexus warpNexus = AddonBlockRegistry.WARP_NEXUS.get();
            LootPool.Builder nexusBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(warpNexus)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                    .copy("Inventory", "BlockEntityTag.Inventory", CopyNbtFunction.MergeStrategy.REPLACE)
                                    .copy("color", "BlockEntityTag.color", CopyNbtFunction.MergeStrategy.REPLACE)
                            )
                    );
            this.list.add(warpNexus);
            this.add(warpNexus, LootTable.lootTable().withPool(nexusBuilder));
        }

        private void registerDropSelf(Block block) {
            this.list.add(block);
            dropSelf(block);
        }

        private <B extends Block, T extends BlockEntity> void registerManaMachine(RegistryObject<B> block, RegistryObject<BlockEntityType<T>> tile) {
            this.list.add(block.get());
            this.add(block.get(), createManaMachineTable(block.get(), tile.get()));
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
