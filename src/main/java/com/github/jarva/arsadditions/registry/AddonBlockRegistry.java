package com.github.jarva.arsadditions.registry;

import com.github.jarva.arsadditions.block.EnderSourceJar;
import com.github.jarva.arsadditions.block.tile.EnderSourceJarTile;
import com.github.jarva.arsadditions.item.EnderSourceJarItem;
import com.github.jarva.arsadditions.registry.names.AddonBlockNames;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;
import static com.github.jarva.arsadditions.registry.AddonItemRegistry.ITEMS;
import static com.github.jarva.arsadditions.registry.AddonItemRegistry.defaultItemProperties;

public class AddonBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static RegistryObject<EnderSourceJar> ENDER_SOURCE_JAR;
    public static RegistryObject<BlockEntityType<EnderSourceJarTile>> ENDER_SOURCE_JAR_TILE;

    static {
        ENDER_SOURCE_JAR = registerBlockAndItem(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJar::new, (block) -> new EnderSourceJarItem(block.get(), defaultItemProperties()));
        ENDER_SOURCE_JAR_TILE = registerTile(AddonBlockNames.ENDER_SOURCE_JAR, EnderSourceJarTile::new, ENDER_SOURCE_JAR);
    }

    public static BlockItem getDefaultBlockItem(Block block) {
        return new BlockItem(block, defaultItemProperties());
    }

    public static <T extends Block> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> blockSupp) {
        return registerBlockAndItem(name, blockSupp, (block) -> getDefaultBlockItem(block.get()));
    }

    public static <T extends Block, R extends BlockItem> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> blockSupp, Function<RegistryObject<T>, R> itemSupp) {
        RegistryObject<T> block = BLOCKS.register(name, blockSupp);
        ITEMS.register(name, () -> itemSupp.apply(block));
        return block;
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTile(String regName, BlockEntityType.BlockEntitySupplier<T> tile, RegistryObject<? extends Block> block){
        return BLOCK_ENTITIES.register(regName, () -> BlockEntityType.Builder.of(tile, block.get()).build(null));
    }
}
