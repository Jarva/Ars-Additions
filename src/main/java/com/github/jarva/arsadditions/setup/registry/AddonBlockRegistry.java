package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.client.renderers.tile.WarpNexusRenderer;
import com.github.jarva.arsadditions.common.block.*;
import com.github.jarva.arsadditions.common.block.tile.*;
import com.github.jarva.arsadditions.common.item.EnderSourceJarItem;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericRenderer;
import com.hollingsworth.arsnouveau.common.items.RendererBlockItem;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;
import static com.github.jarva.arsadditions.setup.registry.AddonItemRegistry.ITEMS;
import static com.github.jarva.arsadditions.setup.registry.AddonItemRegistry.defaultItemProperties;

public class AddonBlockRegistry {
    public static final List<BlockRegistryWrapper<? extends Block>> REGISTERED_BLOCKS = new ArrayList<>();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static BlockEntityTypeRegistryWrapper<MagelightLanternTile> MAGELIGHT_LANTERN_TILE;
    public static BlockRegistryWrapper<EnderSourceJar> ENDER_SOURCE_JAR;
    public static BlockEntityTypeRegistryWrapper<EnderSourceJarTile> ENDER_SOURCE_JAR_TILE;

    public static BlockRegistryWrapper<WarpNexus> WARP_NEXUS;
    public static BlockEntityTypeRegistryWrapper<WarpNexusTile> WARP_NEXUS_TILE;
    public static BlockRegistryWrapper<EnchantingWixieCauldron> WIXIE_ENCHANTING;
    public static BlockEntityTypeRegistryWrapper<EnchantingWixieCauldronTile> WIXIE_ENCHANTING_TILE;
    public static BlockRegistryWrapper<SourceSpawner> SOURCE_SPAWNER;
    public static BlockEntityTypeRegistryWrapper<SourceSpawnerTile> SOURCE_SPAWNER_TILE;

    static {
        WIXIE_ENCHANTING = registerBlockAndItem(AddonBlockNames.ENCHANTING_WIXIE_CAULDRON, EnchantingWixieCauldron::new, (block) -> new RendererBlockItem(block.get(), defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return GenericRenderer.getISTER("enchanting_apparatus");
            }
        });
        WIXIE_ENCHANTING_TILE = registerTile(AddonBlockNames.ENCHANTING_WIXIE_CAULDRON, EnchantingWixieCauldronTile::new, () -> new Block[]{WIXIE_ENCHANTING.get()});

        ENDER_SOURCE_JAR = registerBlockAndItem(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJar::new, (block) -> new EnderSourceJarItem(block.get(), defaultItemProperties()));
        ENDER_SOURCE_JAR_TILE = registerTile(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJarTile::new, () -> new Block[]{ENDER_SOURCE_JAR.get()});

        SOURCE_SPAWNER = registerBlockAndItem(AddonBlockNames.SOURCE_SPAWNER, SourceSpawner::new);
        SOURCE_SPAWNER_TILE = registerTile(AddonBlockNames.SOURCE_SPAWNER, SourceSpawnerTile::new, () -> new Block[]{SOURCE_SPAWNER.get()});

        WARP_NEXUS = registerBlockAndItem(AddonBlockNames.WARP_NEXUS, WarpNexus::new, (block) -> new RendererBlockItem(block.get(), defaultItemProperties()) {
            @Override
            public Supplier<BlockEntityWithoutLevelRenderer> getRenderer() {
                return WarpNexusRenderer::getISTER;
            }
        });
        WARP_NEXUS_TILE = registerTile(AddonBlockNames.WARP_NEXUS, WarpNexusTile::new, () -> new Block[]{WARP_NEXUS.get()});

        registerChains();
        registerMagelightLanterns();
        registerLanterns();
        MAGELIGHT_LANTERN_TILE = registerTile(AddonBlockNames.MAGELIGHT_LANTERN, MagelightLanternTile::new, () -> getBlocks(AddonBlockNames.MAGELIGHT_LANTERNS));
        registerWalls();
        registerButtons();
        registerDecorativeSourcestone();
        registerDoors();
        registerTrapdoors();
        registerCarpets();
    }

    private static void registerCarpets() {
        for (String carpet : AddonBlockNames.CARPETS) {
            registerBlockAndItem(carpet, () -> new CarpetBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_CARPET)));
        }
    }

    private static void registerDecorativeSourcestone() {
        for (String sourcestone : AddonBlockNames.DECORATIVE_SOURCESTONES) {
            registerBlockAndItem(sourcestone, () -> new Block(
                    BlockBehaviour.Properties.ofFullCopy(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE))
            ));
        }
    }

    private static void registerTrapdoors() {
        for (String trapdoor : AddonBlockNames.TRAPDOORS) {
            registerBlockAndItem(trapdoor, () ->
                    new TrapDoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(BlockRegistry.getBlock(LibBlockNames.ARCHWOOD_TRAPDOOR)))
            );
        }
    }

    private static void registerDoors() {
        for (String door : AddonBlockNames.DOORS) {
            registerBlockAndItem(door, () ->
                    new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(BlockRegistry.getBlock(LibBlockNames.ARCHWOOD_DOOR)))
            );
        }
    }

    private static void registerChains() {
        for (String chain : AddonBlockNames.CHAINS) {
            registerBlockAndItem(chain, () -> new ChainBlock(BlockBehaviour.Properties.of().forceSolidOn().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion()));
        }
    }

    private static void registerMagelightLanterns() {
        for (String lantern : AddonBlockNames.MAGELIGHT_LANTERNS) {
            registerBlockAndItem(lantern, MagelightLantern::new);
        }
    }

    private static void registerLanterns() {
        for (String lantern : AddonBlockNames.LANTERNS) {
            registerBlockAndItem(lantern, () -> new LanternBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .forceSolidOn()
                            .requiresCorrectToolForDrops()
                            .strength(3.5F)
                            .sound(SoundType.LANTERN)
                            .lightLevel((arg) -> 15)
                            .noOcclusion()
                            .pushReaction(PushReaction.DESTROY)
            ));
        }
    }

    private static void registerWalls() {
        for (String wall : AddonBlockNames.WALLS) {
            registerBlockAndItem(wall, () -> new WallBlock(
                    BlockBehaviour.Properties.ofFullCopy(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE))
                            .forceSolidOn()
            ));
        }
    }

    private static void registerButtons() {
        for (String button : AddonBlockNames.BUTTONS) {
            registerBlockAndItem(button, () -> new ButtonBlock(
                    BlockSetType.STONE, 20,
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .strength(0.5f)
                            .pushReaction(PushReaction.DESTROY)
            ));
        }
    }

    public static BlockItem getDefaultBlockItem(Block block) {
        return new BlockItem(block, defaultItemProperties());
    }

    public static <T extends Block> BlockRegistryWrapper<T> registerBlockAndItem(String name, Supplier<T> blockSupp) {
        return registerBlockAndItem(name, blockSupp, (block) -> getDefaultBlockItem(block.get()));
    }

    public static <T extends Block, R extends BlockItem> BlockRegistryWrapper<T> registerBlockAndItem(String name, Supplier<T> blockSupp, Function<BlockRegistryWrapper<T>, R> itemSupp) {
        BlockRegistryWrapper<T> block = new BlockRegistryWrapper<>(BLOCKS.register(name, blockSupp));
        REGISTERED_BLOCKS.add(block);
        ITEMS.register(name, () -> itemSupp.apply(block));
        return block;
    }

    public static <T extends BlockEntity> BlockEntityTypeRegistryWrapper<T> registerTile(String regName, BlockEntityType.BlockEntitySupplier<T> tile, Supplier<Block[]> block){
        return new BlockEntityTypeRegistryWrapper<>(BLOCK_ENTITIES.register(regName, () -> BlockEntityType.Builder.of(tile, block.get()).build(null)));
    }

    public static Block getBlock(String s) {
        return BuiltInRegistries.BLOCK.get(ArsAdditions.prefix(s));
    }

    public static Block[] getBlocks(String[] names) {
        return Arrays.stream(names).map(AddonBlockRegistry::getBlock).toList().toArray(new Block[]{});
    }
}
