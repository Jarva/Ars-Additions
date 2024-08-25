package com.github.jarva.arsadditions.datagen.worldgen;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendStatic;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames.CRACKED_SOURCESTONE_LARGE_BRICKS;
import static com.hollingsworth.arsnouveau.common.lib.LibBlockNames.*;

public class ProcessorDatagen extends SimpleDataProvider {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public ProcessorDatagen(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(dataGenerator);
        this.lookupProvider = lookupProvider;
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        List<ProcessorRule> nexusTower = new ArrayList<>();
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, 0.5f));
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, 0.5f));
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.STONE, 0.1f));
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.ANDESITE, 0.1f));
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.TUFF, 0.1f));
        nexusTower.add(randomBlockReplace(Blocks.STONE_BRICKS, Blocks.COBBLESTONE, 0.1f));
        nexusTower.add(randomBlockReplace(an(SOURCESTONE_LARGE_BRICKS), aa(CRACKED_SOURCESTONE_LARGE_BRICKS), 0.5f));
        nexusTower.add(randomBlockReplace(an(SOURCESTONE_LARGE_BRICKS), an(GILDED_SOURCESTONE_LARGE_BRICKS), 0.2f));
        nexusTower.add(randomBlockReplace(an(SOURCESTONE_LARGE_BRICKS), Blocks.BLUE_TERRACOTTA, 0.2f));
        nexusTower.add(randomBlockReplace(an(SOURCESTONE_LARGE_BRICKS), an(SOURCESTONE_ALTERNATING), 0.2f));
        nexusTower.add(randomBlockStateReplace(Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 0.2f));
        nexusTower.add(randomBlockStateReplace(Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM), 0.2f));
        nexusTower.add(randomBlockReplace(an(SOURCESTONE_LARGE_BRICKS), an(SOURCESTONE_ALTERNATING), 0.2f));

        setupWarpNexus(nexusTower);
//        setupSourceJars(nexusTower);

        List<Helper> helpers = List.of(
                new Helper("Eru", "gray", "Warning: Not to leave unsupervised"),
                new Helper("Rozeta", "pink", "A curious Starbuncle that likes to build and experiment with magic."),
                new Helper("Pug", "green", "A Starbuncle that sells the magic it salvages.")
        );
        HashMap<String, Integer> dyeMap = new HashMap<>();
        for (DyeColor value : DyeColor.values()) {
            dyeMap.put(value.getName(), value.getTextColor());
        }

        lookupProvider.thenAccept(provider -> {
            for (Helper helper : helpers) {
                CompoundTag tag = new CompoundTag();
                ItemStack is = new ItemStack(ItemsRegistry.STARBUNCLE_CHARM);
                is.set(DataComponentRegistry.STARBUNCLE_DATA, new StarbuncleCharmData(Optional.of(Component.literal(helper.name()).withStyle(Style.EMPTY.withColor(dyeMap.get(helper.color())))), helper.color(), Optional.empty(), Optional.empty(), StarbyTransportBehavior.TRANSPORT_ID, new CompoundTag(), "", helper.bio()));
                tag.put("itemStack", is.save(provider));
                modifyBlockEntity(BlockRegistry.SCRIBES_BLOCK.get(), bs -> bs.getValue(ScribesBlock.PART) == ThreePartBlock.HEAD, 0.5f, new AppendStatic(tag), nexusTower);
            }
        }).join();

        save(pOutput, nexusTower, "nexus_tower");

        List<ProcessorRule> arcaneLibrary = new ArrayList<>();

        chargeRunes(arcaneLibrary);
        setupWarpNexus(arcaneLibrary);
        setupSourceJars(arcaneLibrary);

        save(pOutput, arcaneLibrary, "arcane_library");
    }

    private void setupSourceJars(List<ProcessorRule> rules) {
        for (Integer possibleValue : SourceJar.fill.getPossibleValues()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("source", Math.min((possibleValue - 1) * 1000, 0));
            modifyBlockEntity(BlockRegistry.SOURCE_JAR.get(), bs -> true, bs -> bs.setValue(SourceJar.fill, possibleValue), new AppendStatic(tag), rules);
        }
    }

    private void setupWarpNexus(List<ProcessorRule> rules) {
        CompoundTag nexusBlock = new CompoundTag();
        ItemStack stack = new ItemStack(AddonItemRegistry.NEXUS_WARP_SCROLL.get());
        lookupProvider.thenAccept(provider ->
                nexusBlock.put("itemStack", stack.save(provider))
        ).join();

        modifyBlockEntity(AddonBlockRegistry.WARP_NEXUS.get(), bs -> bs.getValue(WarpNexus.HALF) == DoubleBlockHalf.LOWER, bs -> bs.setValue(WarpNexus.REQUIRES_SOURCE, false), new AppendStatic(nexusBlock), rules);
        modifyBlockEntity(AddonBlockRegistry.WARP_NEXUS.get(), bs -> bs.getValue(WarpNexus.HALF) == DoubleBlockHalf.UPPER, bs -> bs.setValue(WarpNexus.REQUIRES_SOURCE, false), new AppendStatic(new CompoundTag()), rules);
    }

    private void chargeRunes(List<ProcessorRule> rules) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("charged", true);
        tag.putBoolean("temporary", false);
        tag.putUUID("uuid", ANFakePlayer.PROFILE.getId());
        modifyBlockEntity(BlockRegistry.RUNE_BLOCK.get(), bs -> !bs.getValue(RuneBlock.POWERED), bs -> bs.setValue(RuneBlock.POWERED, true), 1f, new AppendStatic(tag), rules);
    }

    record Helper(String name, String color, String bio) {}

    private void modifyBlockEntity(Block block, Predicate<BlockState> filter, Function<BlockState, BlockState> output, RuleBlockEntityModifier modifier, List<ProcessorRule> rules) {
        modifyBlockEntity(block, filter, output, null, modifier, rules);
    }

    private void modifyBlockEntity(Block block, Predicate<BlockState> filter, float probability, RuleBlockEntityModifier modifier, List<ProcessorRule> rules) {
        modifyBlockEntity(block, filter, bs -> bs, probability, modifier, rules);
    }

    private void modifyBlockEntity(Block block, Predicate<BlockState> filter, Function<BlockState, BlockState> output, Float probability, RuleBlockEntityModifier modifier, List<ProcessorRule> rules) {
        for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
            if (filter.test(possibleState)) {
                RuleTest test = probability != null ? new RandomBlockStateMatchTest(possibleState, probability) : new BlockStateMatchTest(possibleState);
                rules.add(
                        new ProcessorRule(
                                test,
                                AlwaysTrueTest.INSTANCE,
                                PosAlwaysTrueTest.INSTANCE,
                                output.apply(possibleState),
                                modifier
                        )
                );
            }
        }
    }

    private void save(CachedOutput pOutput, List<ProcessorRule> rules, String name) {
        StructureProcessorList list = new StructureProcessorList(ImmutableList.of(new RuleProcessor(rules)));
        DataResult<JsonElement> result = StructureProcessorType.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, list);
        result.result().ifPresent(element -> {
            saveStable(pOutput, element, getPath(name));
        });
    }

    private Path getPath(String name) {
        return output.resolve("data/ars_additions/worldgen/processor_list/" + name + ".json");
    }

    private Block aa(String name) {
        return AddonBlockRegistry.getBlock(name);
    }

    private Block an(String name) {
        return BlockRegistry.getBlock(name);
    }

    private ProcessorRule randomBlockStateReplace(BlockState input, Block output, float probability) {
        return randomBlockStateReplace(input, output.defaultBlockState(), probability);
    }
    private ProcessorRule randomBlockStateReplace(BlockState input, BlockState output, float probability) {
        return new ProcessorRule(
                new RandomBlockStateMatchTest(input, probability),
                AlwaysTrueTest.INSTANCE,
                output
        );
    }

    private ProcessorRule randomBlockReplace(Block input, Block output, float probability) {
        return randomBlockReplace(input, output.defaultBlockState(), probability);
    }
    private ProcessorRule randomBlockReplace(Block input, BlockState output, float probability) {
        return new ProcessorRule(
                new RandomBlockMatchTest(input, probability),
                AlwaysTrueTest.INSTANCE,
                output
        );
    }

    @Override
    public String getName() {
        return "Processor Lists";
    }
}
