package com.github.jarva.arsadditions.datagen.client;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.block.MagelightLantern;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.names.AddonBlockNames;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStateDatagen extends BlockStateProvider {
    private final ExistingFileHelper fileHelper;

    public BlockStateDatagen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ArsAdditions.MODID, exFileHelper);
        this.fileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for (Supplier<Glyph> i : GlyphRegistry.getGlyphItemMap().values()) {
            ResourceLocation spellPart = i.get().spellPart.getRegistryName();
            if (!spellPart.getNamespace().equals(ArsAdditions.MODID)) continue;
            itemModels().basicItem(spellPart);
        }

        for (RitualTablet tablet : RitualRegistry.getRitualItemMap().values()) {
            ResourceLocation ritualName = tablet.ritual.getRegistryName();
            if (!ritualName.getNamespace().equals(ArsAdditions.MODID)) continue;
            itemModels().basicItem(tablet);
        }

        for (RegistryObject<Item> item : AddonItemRegistry.DATAGEN_ITEMS) {
            itemModels().basicItem(item.get());
        }

        Block[] decorativeSourcestone = AddonBlockRegistry.getBlocks(AddonBlockNames.DECORATIVE_SOURCESTONES);
        for (Block block : decorativeSourcestone) {
            simpleBlockWithItem(block);
        }

        simpleBlockWithItem(AddonBlockRegistry.SOURCE_SPAWNER.get(), "cutout");

        wallBlockAndItem(AddonBlockNames.SOURCESTONE_WALL, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.SOURCESTONE_LARGE_BRICKS));
        wallBlockAndItem(AddonBlockNames.POLISHED_SOURCESTONE_WALL, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.SMOOTH_SOURCESTONE_LARGE_BRICKS));
        wallBlockAndItem(AddonBlockNames.CRACKED_SOURCESTONE_WALL, new ResourceLocation(ArsAdditions.MODID, AddonBlockNames.CRACKED_SOURCESTONE));
        wallBlockAndItem(AddonBlockNames.CRACKED_POLISHED_SOURCESTONE_WALL, new ResourceLocation(ArsAdditions.MODID, AddonBlockNames.CRACKED_POLISHED_SOURCESTONE));

        buttonBlockAndItem(AddonBlockNames.SOURCESTONE_BUTTON, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.SOURCESTONE));
        buttonBlockAndItem(AddonBlockNames.POLISHED_SOURCESTONE_BUTTON, new ResourceLocation(ArsNouveau.MODID, LibBlockNames.SMOOTH_SOURCESTONE));

        for (String lantern : AddonBlockNames.LANTERNS) {
            lanternAndItem(lantern);
        }
        for (String magelightLantern : AddonBlockNames.MAGELIGHT_LANTERNS) {
            lanternAndItem(magelightLantern);
        }
        for (String chain : AddonBlockNames.CHAINS) {
            chainAndItem(chain);
        }

        ModelFile enchantingApparatus = new ModelFile.ExistingModelFile(getTextureLoc(BlockRegistry.ENCHANTING_APP_BLOCK.get()), fileHelper);
        getVariantBuilder(AddonBlockRegistry.WIXIE_ENCHANTING.get()).partialState().setModels(new ConfiguredModel(enchantingApparatus));
        simpleBlockItem(AddonBlockRegistry.WIXIE_ENCHANTING.get(), new ModelFile.ExistingModelFile(key(BlockRegistry.ENCHANTING_APP_BLOCK.get()).withPrefix("item/"), fileHelper));

        for (String door : AddonBlockNames.DOORS) {
            doorAndItem(door);
        }

        for (String trapdoor : AddonBlockNames.TRAPDOORS) {
            trapdoorAndItem(trapdoor);
        }

        for (String carpet: AddonBlockNames.CARPETS) {
            carpetAndItem(carpet);
        }
    }

    private void carpetAndItem(String carpetName) {
        CarpetBlock block = (CarpetBlock) AddonBlockRegistry.getBlock(carpetName);
        ResourceLocation loc = key(block);
        ModelFile model = models().withExistingParent(loc.withPrefix("block/").toString(), mcLoc("block/carpet"))
                .texture("wool", getTextureLoc(block));
        getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(model).build());
        itemModels().withExistingParent(loc.withPrefix("item/").toString(), model.getLocation());
    }

    private void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, (String) null);
    }

    private void simpleBlockWithItem(Block block, String renderType) {
        BlockModelBuilder model = (BlockModelBuilder) cubeAll(block);
        if (renderType != null) {
            model = model.renderType(renderType);
        }
        simpleBlockWithItem(block, model);
    }

    private void doorAndItem(String doorName) {
        DoorBlock block = (DoorBlock) AddonBlockRegistry.getBlock(doorName);
        doorBlockWithRenderType(block, getTextureLoc(block).withSuffix("_bottom"), getTextureLoc(block).withSuffix("_top"), "cutout");
        itemModels().basicItem(block.asItem());
    }

    private void trapdoorAndItem(String trapdoorName) {
        TrapDoorBlock block = (TrapDoorBlock) AddonBlockRegistry.getBlock(trapdoorName);
        trapdoorBlockWithRenderType(block, getTextureLoc(block), true, "cutout");
        ResourceLocation loc = key(block);
        itemModels().withExistingParent(loc.toString(), getTextureLoc(block).withSuffix("_bottom"));
    }

    private void chainAndItem(String chainName) {
        ChainBlock block = (ChainBlock) AddonBlockRegistry.getBlock(chainName);
        ResourceLocation texture = getTextureLoc(block);
        ModelFile chain = models().withExistingParent(key(block).getPath(), mcLoc(BLOCK_FOLDER + "/chain"))
                .texture("particle", texture)
                .texture("all", texture)
                .renderType("cutout");
        getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction.Axis axis = state.getValue(ChainBlock.AXIS);
            return ConfiguredModel.builder()
                    .modelFile(chain)
                    .rotationX(axis == Direction.Axis.X || axis == Direction.Axis.Z ? 90 : 0)
                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                    .build();
        }, ChainBlock.WATERLOGGED);
        itemModels().basicItem(block.asItem());
    }

    private void lanternAndItem(String lanternName) {
        LanternBlock block = (LanternBlock) AddonBlockRegistry.getBlock(lanternName);
        ResourceLocation texture = getTextureLoc(block);
        ModelFile lantern = lanternModel(block, false, texture);
        ModelFile lanternHanging = lanternModel(block, true, texture);
        getVariantBuilder(block).forAllStatesExcept(state -> {
            boolean hanging = state.getValue(LanternBlock.HANGING);

            return ConfiguredModel.builder()
                    .modelFile(hanging ? lanternHanging : lantern)
                    .build();
        }, LanternBlock.WATERLOGGED, MagelightLantern.LIGHT_LEVEL);
        itemModels().basicItem(block.asItem());
    }

    private ModelFile lanternModel(Block block, boolean isHanging, ResourceLocation texture) {
        String path = key(block).getPath();
        String name = isHanging ? path + "_hanging" : path;
        return models().singleTexture(name,
                mcLoc(BLOCK_FOLDER + (isHanging ? "/template_hanging_lantern" : "/template_lantern")),
                "lantern",
                texture
        ).renderType("cutout");
    }

    private void wallBlockAndItem(String wallName, ResourceLocation texture) {
        WallBlock block = (WallBlock) AddonBlockRegistry.getBlock(wallName);
        texture = texture.withPrefix("block/");
        String name = ForgeRegistries.BLOCKS.getKey(block).toString().replace("_wall", "");
        wallBlock(block, name, texture);
        ModelFile inventory = models().wallInventory(name + "_inventory", texture);
        simpleBlockItem(block, inventory);
    }

    private void buttonBlockAndItem(String buttonName, ResourceLocation texture) {
        ButtonBlock block = (ButtonBlock) AddonBlockRegistry.getBlock(buttonName);
        texture = texture.withPrefix("block/");
        String name = ForgeRegistries.BLOCKS.getKey(block).toString();
        buttonBlock(block, texture);
        ModelFile inventory = models().buttonInventory(name + "_inventory", texture);
        simpleBlockItem(block, inventory);
    }

    public ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public ResourceLocation getTextureLoc(Block block) {
        return key(block).withPrefix("block/");
    }
}
