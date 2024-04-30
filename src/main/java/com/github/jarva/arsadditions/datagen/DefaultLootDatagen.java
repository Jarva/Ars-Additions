package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.common.block.EnchantingWixieCauldron;
import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry.getBlock;

public class DefaultLootDatagen extends LootTableProvider {
    public DefaultLootDatagen(DataGenerator packOutput) {
        super(packOutput);
    }

    public static class BlockLootTableProvider extends BlockLoot {
        public List<Block> list = new ArrayList<>();

        @Override
        public void addTables() {
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

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return list;
        }
    }

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(
            Pair.of(BlockLootTableProvider::new, LootContextParamSets.BLOCK)
    );

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
        });
    }
}
