package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.common.block.EnchantingWixieCauldron;
import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public class DefaultLootDatagen extends LootTableProvider {
    public DefaultLootDatagen(PackOutput packOutput) {
        super(packOutput, new HashSet<>(), List.of(new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class BlockLootTableProvider extends BlockLootSubProvider {
        private final List<Block> list = new ArrayList<>();
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

            String[][] nameList = new String[][]{
                    AddonBlockNames.CHAINS, AddonBlockNames.MAGELIGHT_LANTERNS,
                    AddonBlockNames.LANTERNS, AddonBlockNames.BUTTONS, AddonBlockNames.DECORATIVE_SOURCESTONES,
                    AddonBlockNames.WALLS, AddonBlockNames.DOORS, AddonBlockNames.TRAPDOORS,
                    AddonBlockNames.CARPETS
            };
            for (String[] names : nameList) {
                for (Block block : AddonBlockRegistry.getBlocks(names)) {
                    registerDropSelf(block);
                }
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
