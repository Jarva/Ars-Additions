package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.block.EnderSourceJar;
import com.github.jarva.arsadditions.block.MagelightLantern;
import com.github.jarva.arsadditions.block.tile.EnderSourceJarTile;
import com.github.jarva.arsadditions.block.tile.MagelightLanternTile;
import com.github.jarva.arsadditions.item.EnderSourceJarItem;
import com.github.jarva.arsadditions.registry.names.AddonBlockNames;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;
import static com.github.jarva.arsadditions.registry.AddonItemRegistry.ITEMS;
import static com.github.jarva.arsadditions.registry.AddonItemRegistry.defaultItemProperties;

public class AddonBlockRegistry {
    public static final List<RegistryObject<? extends Block>> REGISTERED_BLOCKS = new ArrayList<>();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static RegistryObject<EnderSourceJar> ENDER_SOURCE_JAR;
    public static RegistryObject<BlockEntityType<EnderSourceJarTile>> ENDER_SOURCE_JAR_TILE;

    public static RegistryObject<ChainBlock> ARCHWOOD_CHAIN;
    public static RegistryObject<ChainBlock> SOURCESTONE_CHAIN;
    public static RegistryObject<ChainBlock> POLISHED_SOURCESTONE_CHAIN;
    public static RegistryObject<ChainBlock> GOLDEN_CHAIN;

    public static RegistryObject<MagelightLantern> GOLDEN_MAGELIGHT_LANTERN;
    public static RegistryObject<MagelightLantern> ARCHWOOD_MAGELIGHT_LANTERN;
    public static RegistryObject<MagelightLantern> SOURCESTONE_MAGELIGHT_LANTERN;
    public static RegistryObject<MagelightLantern> POLISHED_SOURCESTONE_MAGELIGHT_LANTERN;
    public static RegistryObject<MagelightLantern> SOUL_MAGELIGHT_LANTERN;
    public static RegistryObject<MagelightLantern> MAGELIGHT_LANTERN;
    public static RegistryObject<BlockEntityType<MagelightLanternTile>> MAGELIGHT_LANTERN_TILE;

    public static RegistryObject<LanternBlock> GOLDEN_LANTERN;
    public static RegistryObject<LanternBlock> ARCHWOOD_LANTERN;
    public static RegistryObject<LanternBlock> SOURCESTONE_LANTERN;
    public static RegistryObject<LanternBlock> POLISHED_SOURCESTONE_LANTERN;

    static {
        ENDER_SOURCE_JAR = registerBlockAndItem(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJar::new, (block) -> new EnderSourceJarItem(block.get(), defaultItemProperties()));
        ENDER_SOURCE_JAR_TILE = registerTile(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJarTile::new, ENDER_SOURCE_JAR);

        ARCHWOOD_CHAIN = registerBlockAndItem(AddonBlockNames.ARCHWOOD_CHAIN, () -> new ChainBlock(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion()));
        SOURCESTONE_CHAIN = registerBlockAndItem(AddonBlockNames.SOURCESTONE_CHAIN, () -> new ChainBlock(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion()));
        POLISHED_SOURCESTONE_CHAIN = registerBlockAndItem(AddonBlockNames.POLISHED_SOURCESTONE_CHAIN, () -> new ChainBlock(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion()));
        GOLDEN_CHAIN = registerBlockAndItem(AddonBlockNames.GOLDEN_CHAIN, () -> new ChainBlock(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion()));

        GOLDEN_MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.GOLDEN_MAGELIGHT_LANTERN, MagelightLantern::new);
        ARCHWOOD_MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.ARCHWOOD_MAGELIGHT_LANTERN, MagelightLantern::new);
        SOURCESTONE_MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.SOURCESTONE_MAGELIGHT_LANTERN, MagelightLantern::new);
        POLISHED_SOURCESTONE_MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.POLISHED_SOURCESTONE_MAGELIGHT_LANTERN, MagelightLantern::new);
        SOUL_MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.SOUL_MAGELIGHT_LANTERN, MagelightLantern::new);
        MAGELIGHT_LANTERN = registerBlockAndItem(AddonBlockNames.MAGELIGHT_LANTERN, MagelightLantern::new);

        MAGELIGHT_LANTERN_TILE = registerTile(AddonBlockNames.MAGELIGHT_LANTERN, MagelightLanternTile::new, GOLDEN_MAGELIGHT_LANTERN, ARCHWOOD_MAGELIGHT_LANTERN, SOURCESTONE_MAGELIGHT_LANTERN, POLISHED_SOURCESTONE_MAGELIGHT_LANTERN, SOUL_MAGELIGHT_LANTERN, MAGELIGHT_LANTERN);
        GOLDEN_LANTERN = registerBlockAndItem(AddonBlockNames.GOLDEN_LANTERN, AddonBlockRegistry::createLantern);
        ARCHWOOD_LANTERN = registerBlockAndItem(AddonBlockNames.ARCHWOOD_LANTERN, AddonBlockRegistry::createLantern);
        SOURCESTONE_LANTERN = registerBlockAndItem(AddonBlockNames.SOURCESTONE_LANTERN, AddonBlockRegistry::createLantern);
        POLISHED_SOURCESTONE_LANTERN = registerBlockAndItem(AddonBlockNames.POLISHED_SOURCESTONE_LANTERN, AddonBlockRegistry::createLantern);
    }

    public static LanternBlock createLantern() {
        return new LanternBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .forceSolidOn()
                        .requiresCorrectToolForDrops()
                        .strength(3.5F)
                        .sound(SoundType.LANTERN)
                        .lightLevel((arg) -> 15)
                        .noOcclusion()
                        .pushReaction(PushReaction.DESTROY)
        );
    }

    public static BlockItem getDefaultBlockItem(Block block) {
        return new BlockItem(block, defaultItemProperties());
    }

    public static <T extends Block> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> blockSupp) {
        return registerBlockAndItem(name, blockSupp, (block) -> getDefaultBlockItem(block.get()));
    }

    public static <T extends Block, R extends BlockItem> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> blockSupp, Function<RegistryObject<T>, R> itemSupp) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupp);
        REGISTERED_BLOCKS.add(block);
        ITEMS.register(name, () -> itemSupp.apply(block));
        return block;
    }

    public static <T extends BlockEntity, B extends Block> RegistryObject<BlockEntityType<T>> registerTile(String regName, BlockEntityType.BlockEntitySupplier<T> tile, RegistryObject<? extends Block>... block){
        return BLOCK_ENTITIES.register(regName, () -> BlockEntityType.Builder.of(tile, Arrays.stream(block).map(RegistryObject::get).toArray(Block[]::new)).build(null));
    }
}
