package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.server.storage.EnderSourceData;
import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
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

        int itemSource = BlockFillContents.get(stack);
        int source = EnderSourceData.getSource(level.getServer(), entity.getUUID());

        if (itemSource != source) {
            stack.set(DataComponentRegistry.BLOCK_FILL_CONTENTS, new BlockFillContents(source));
        }
    }
}
