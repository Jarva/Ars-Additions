package com.github.jarva.arsadditions.item;

import com.github.jarva.arsadditions.util.MarkType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class UnstableReliquary extends Item {
    public UnstableReliquary() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String markType = tag.getString("mark_type");
        MarkType mark = MarkType.valueOf(markType);
        CompoundTag data = tag.getCompound("mark_data");
        if (mark == MarkType.ENTITY) {
            if (level instanceof ServerLevel serverLevel) {
                UUID uuid = data.getUUID("entity_uuid");
                Entity found = serverLevel.getEntity(uuid);
                if (found == null || !found.isAlive()) {
                    this.breakReliquary(stack);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.empty"));
            return;
        };

        String markType = tag.getString("mark_type");
        MarkType mark = MarkType.valueOf(markType);
        CompoundTag data = tag.getCompound("mark_data");

        if (mark == MarkType.ENTITY) {
            String type = data.getString("entity_type");
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.entity"));
            tooltipComponents.add(Component.translatable(type));
        }

        if (mark == MarkType.LOCATION) {
            BlockPos pos = NbtUtils.readBlockPos(data.getCompound("block_pos"));
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.location"));
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.location.pos", pos.getX(), pos.getY(), pos.getZ()));
        }

        if (mark == MarkType.BROKEN) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.broken"));
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.empty"));
        }

        if (mark == MarkType.EMPTY) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.empty"));
        }
    }

    public void breakReliquary(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        tag.putString("mark_type", MarkType.BROKEN.toString());
        tag.put("mark_data", new CompoundTag());
    }
}
