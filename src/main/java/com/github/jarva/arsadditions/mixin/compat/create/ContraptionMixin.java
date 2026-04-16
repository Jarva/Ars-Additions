package com.github.jarva.arsadditions.mixin.compat.create;

import com.github.jarva.arsadditions.common.block.EnderSourceJar;
import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile.OWNER_UUID_TAG;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @ModifyVariable(method = "readBlocksCompound", at = @At("STORE"), name = "info")
    private StructureTemplate.StructureBlockInfo setJarInfo(StructureTemplate.StructureBlockInfo info, @Local(argsOnly = true) Level level) {
        if (level instanceof ServerLevel serverLevel && info.nbt() != null && info.state().getBlock() instanceof EnderSourceJar)
            return new StructureTemplate.StructureBlockInfo(info.pos(), AddonBlockRegistry.ENDER_SOURCE_JAR.defaultBlockState().setValue(SourceJar.fill, ContraptionUtils.getFillState(EnderSourceData.getSource(serverLevel.getServer(), info.nbt().getUUID(OWNER_UUID_TAG)))), info.nbt());
        return info;
    }
}