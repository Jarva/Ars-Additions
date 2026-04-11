package com.github.jarva.arsadditions.mixin.compat.create;

import com.github.jarva.arsadditions.common.block.EnderSourceJar;
import com.github.jarva.arsadditions.compat.ars_creo.EnderSourceJarUpdateEvent;
import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile.OWNER_UUID_TAG;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow
    public abstract Map<BlockPos, StructureTemplate.StructureBlockInfo> getBlocks();

    @Shadow
    public AbstractContraptionEntity entity;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerListener(CallbackInfo ci) {
        NeoForge.EVENT_BUS.addListener(this::ars_Additions_1_21_0$syncEnderJars);
    }

    @ModifyVariable(method = "readBlocksCompound", at = @At("STORE"), name = "info")
    private StructureTemplate.StructureBlockInfo asd(StructureTemplate.StructureBlockInfo info, @Local(argsOnly = true) Level level) {
        if (level instanceof ServerLevel serverLevel && info.nbt() != null && info.state().getBlock() instanceof EnderSourceJar)
            return new StructureTemplate.StructureBlockInfo(info.pos(), AddonBlockRegistry.ENDER_SOURCE_JAR.defaultBlockState().setValue(SourceJar.fill, ContraptionUtils.getFillState(EnderSourceData.getSource(serverLevel.getServer(), info.nbt().getUUID(OWNER_UUID_TAG)))), info.nbt());
        return info;
    }

    @Unique
    private void ars_Additions_1_21_0$syncEnderJars(EnderSourceJarUpdateEvent event) {
        if (entity == null || entity.level().isClientSide)
            return;
        for (StructureTemplate.StructureBlockInfo blockInfo : getBlocks().values())
            if (blockInfo.state().getBlock() instanceof EnderSourceJar)
                entity.setBlock(blockInfo.pos(), new StructureTemplate.StructureBlockInfo(blockInfo.pos(), AddonBlockRegistry.ENDER_SOURCE_JAR.defaultBlockState().setValue(SourceJar.fill, ContraptionUtils.getFillState(event.source)), blockInfo.nbt()));
    }
}