package com.github.jarva.arsadditions.mixin.compat.create;

import com.github.jarva.arsadditions.common.block.EnderSourceJar;
import com.github.jarva.arsadditions.server.sync.SourceJarSync;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.BiConsumer;

import static com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile.OWNER_UUID_TAG;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract EntityType<?> getType();
    @Unique
    private final BiConsumer<UUID, Integer> ars_Additions_1_21_0$listener = this::ars_Additions_1_21_0$syncEnderJars;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void registerListener(EntityType<?> entityType, Level level, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (!level.isClientSide && self instanceof AbstractContraptionEntity)
            SourceJarSync.contraptions.add(ars_Additions_1_21_0$listener);
    }

    @Inject(method = "setRemoved", at = @At("TAIL"))
    public void unregisterListener(Entity.RemovalReason removalReason, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (self instanceof AbstractContraptionEntity)
            SourceJarSync.contraptions.remove(ars_Additions_1_21_0$listener);
    }

    @Unique
    private void ars_Additions_1_21_0$syncEnderJars(UUID uuid, int source) {
        if (!((Entity)(Object)this instanceof AbstractContraptionEntity entity) || entity.getContraption() == null)
            return;
        for (StructureTemplate.StructureBlockInfo blockInfo : entity.getContraption().getBlocks().values())
            if (blockInfo.state().getBlock() instanceof EnderSourceJar && blockInfo.nbt() != null && blockInfo.nbt().hasUUID(OWNER_UUID_TAG) && blockInfo.nbt().getUUID(OWNER_UUID_TAG).equals(uuid))
                entity.setBlock(blockInfo.pos(), new StructureTemplate.StructureBlockInfo(blockInfo.pos(), AddonBlockRegistry.ENDER_SOURCE_JAR.defaultBlockState().setValue(SourceJar.fill, ContraptionUtils.getFillState(source)), blockInfo.nbt()));
    }
}