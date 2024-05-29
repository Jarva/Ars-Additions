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
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
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
import static com.github.jarva.arsadditions.setup.registry.AddonItemRegistry.ITEMS;
import static com.github.jarva.arsadditions.setup.registry.AddonItemRegistry.defaultItemProperties;

public class AddonBlockRegistry {
    public static final List<RegistryObject<? extends Block>> REGISTERED_BLOCKS = new ArrayList<>();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static RegistryObject<BlockEntityType<MagelightLanternTile>> MAGELIGHT_LANTERN_TILE;
    public static RegistryObject<EnderSourceJar> ENDER_SOURCE_JAR;
    public static RegistryObject<BlockEntityType<EnderSourceJarTile>> ENDER_SOURCE_JAR_TILE;

    public static RegistryObject<WarpNexus> WARP_NEXUS;
    public static RegistryObject<BlockEntityType<WarpNexusTile>> WARP_NEXUS_TILE;
    public static RegistryObject<EnchantingWixieCauldron> WIXIE_ENCHANTING;
    public static RegistryObject<BlockEntityType<EnchantingWixieCauldronTile>> WIXIE_ENCHANTING_TILE;
    public static RegistryObject<SourceSpawner> SOURCE_SPAWNER;
    public static RegistryObject<BlockEntityType<SourceSpawnerTile>> SOURCE_SPAWNER_TILE;

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
            registerBlockAndItem(carpet, () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET)));
        }
    }

    private static void registerDecorativeSourcestone() {
        for (String sourcestone : AddonBlockNames.DECORATIVE_SOURCESTONES) {
            registerBlockAndItem(sourcestone, () -> new Block(
                    BlockBehaviour.Properties.copy(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE))
            ));
        }
    }

    private static void registerTrapdoors() {
        for (String trapdoor : AddonBlockNames.TRAPDOORS) {
            registerBlockAndItem(trapdoor, () ->
                    new TrapDoorBlock(BlockBehaviour.Properties.copy(BlockRegistry.getBlock(LibBlockNames.ARCHWOOD_TRAPDOOR)), BlockSetType.STONE)
            );
        }
    }

    private static void registerDoors() {
        for (String door : AddonBlockNames.DOORS) {
            registerBlockAndItem(door, () ->
                    new DoorBlock(BlockBehaviour.Properties.copy(BlockRegistry.getBlock(LibBlockNames.ARCHWOOD_DOOR)), BlockSetType.STONE)
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
                    BlockBehaviour.Properties.copy(BlockRegistry.getBlock(LibBlockNames.SOURCESTONE))
                            .forceSolidOn()
            ));
        }
    }

    private static void registerButtons() {
        for (String button : AddonBlockNames.BUTTONS) {
            registerBlockAndItem(button, () -> new ButtonBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .strength(0.5f)
                            .pushReaction(PushReaction.DESTROY)
                    , BlockSetType.STONE, 20, false
            ));
        }
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

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTile(String regName, BlockEntityType.BlockEntitySupplier<T> tile, Supplier<Block[]> block){
        return BLOCK_ENTITIES.register(regName, () -> BlockEntityType.Builder.of(tile, block.get()).build(null));
    }

    public static Block getBlock(String s) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ArsAdditions.MODID, s));
    }

    public static Block[] getBlocks(String[] names) {
        return Arrays.stream(names).map(AddonBlockRegistry::getBlock).toList().toArray(new Block[]{});
    }
}
