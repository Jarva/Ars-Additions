package com.github.jarva.arsadditions.item;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.storage.EnderSourceData;
import com.github.jarva.arsadditions.util.FillUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class EnderSourceJarItem extends BlockItem {
    public EnderSourceJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;

        if (level.getGameTime() % 10 != 0) return;

        CompoundTag BET = getBlockEntityData(stack);
        if (BET == null) {
            BET = new CompoundTag();
            BET.putInt("max_source", FillUtil.getMaxSource());
        }

        int source = EnderSourceData.getSource(level.getServer(), entity.getUUID());
        if (BET.contains("source") && BET.getInt("source") == source) {
            return;
        }

        BET.putInt("source", source);

        setBlockEntityData(stack, AddonBlockRegistry.ENDER_SOURCE_JAR_TILE.get(), BET);
    }
}
