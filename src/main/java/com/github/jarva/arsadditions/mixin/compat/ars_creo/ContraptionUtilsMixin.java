package com.github.jarva.arsadditions.mixin.compat.ars_creo;

import com.github.jarva.arsadditions.common.block.EnderSourceJar;
import com.github.jarva.arsadditions.compat.ars_creo.EnderSourceInfo;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.ars_creo.contraption.source.SourceInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ContraptionUtils.class)
public class ContraptionUtilsMixin {
    @ModifyExpressionValue(method = "getSourceBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 0))
    private static Block addEnderSourceJar(Block block, @Local(argsOnly = true) Contraption contraption, @Local(name = "blockInfo") StructureTemplate.StructureBlockInfo blockInfo, @Local(name = "sourceBlocks") List<SourceInfo> sourceBlocks) {
        if (!(block instanceof EnderSourceJar))
            return block;

        sourceBlocks.add(new EnderSourceInfo(blockInfo, contraption.entity.getServer()));
        return null;
    }
}