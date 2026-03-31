package com.github.jarva.arsadditions.compat.ars_creo;

import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.ars_creo.contraption.ContraptionUtils;
import com.hollingsworth.ars_creo.contraption.source.SourceInfo;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import static com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile.OWNER_UUID_TAG;

public class EnderSourceInfo extends SourceInfo {
    private final AbstractContraptionEntity contraptionEntity;

    public EnderSourceInfo(StructureTemplate.StructureBlockInfo blockInfo, AbstractContraptionEntity contraptionEntity) {
        super(blockInfo, 0);
        this.contraptionEntity = contraptionEntity;
    }

    @Override
    public int getAmount() {
        if (this.blockInfo.nbt() == null)
            return 0;
        this.amount = EnderSourceData.getSource(contraptionEntity.getServer(), this.blockInfo.nbt().getUUID(OWNER_UUID_TAG));
        return amount;
    }

    @Override
    public void removeAmount(int amount) {
        if (this.blockInfo.nbt() == null)
            return;
        EnderSourceData.setSource(contraptionEntity.getServer(), this.blockInfo.nbt().getUUID(OWNER_UUID_TAG),
                EnderSourceData.getSource(contraptionEntity.getServer(), this.blockInfo.nbt().getUUID(OWNER_UUID_TAG)) - amount);
        this.amount -= amount;
    }

    @Override
    public void addAmount(int amount) {
        if (this.blockInfo.nbt() == null)
            return;
        EnderSourceData.setSource(contraptionEntity.getServer(), this.blockInfo.nbt().getUUID(OWNER_UUID_TAG),
                EnderSourceData.getSource(contraptionEntity.getServer(), this.blockInfo.nbt().getUUID(OWNER_UUID_TAG)) + amount);
        this.amount += amount;
    }

    @Override
    public void removeWithUpdate(Level level, int amount, AbstractContraptionEntity entity) {
        int currentFillState = ContraptionUtils.getFillState(this.getAmount());
        this.removeAmount(amount);
        int nextFillState = ContraptionUtils.getFillState(this.amount);
        if (currentFillState != nextFillState) {
            this.syncSource(entity, nextFillState);
        }
    }

    @Override
    public void addWithUpdate(Level level, int amount, AbstractContraptionEntity entity) {
        int currentFillState = ContraptionUtils.getFillState(this.getAmount());
        this.addAmount(amount);
        int nextFillState = ContraptionUtils.getFillState(this.amount);
        if (currentFillState != nextFillState) {
            this.syncSource(entity, nextFillState);
        }
    }

    @Override
    public void syncSource(AbstractContraptionEntity contraption, int nextFillState) {
        BlockPos structurePos = this.blockInfo.pos();
        CompoundTag structureTag = this.blockInfo.nbt();
        contraption.setBlock(structurePos, new StructureTemplate.StructureBlockInfo(structurePos, AddonBlockRegistry.ENDER_SOURCE_JAR.defaultBlockState().setValue(SourceJar.fill, nextFillState), structureTag));
    }

    @SuppressWarnings("all")
    public void syncEventListener(EnderSourceJarUpdateEvent event) {
        if (event.uuid.equals(blockInfo.nbt().getUUID(OWNER_UUID_TAG))) {
            amount = event.source;
            syncSource(contraptionEntity, ContraptionUtils.getFillState(amount));
        }
    }
}
