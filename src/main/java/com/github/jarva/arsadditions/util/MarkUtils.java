package com.github.jarva.arsadditions.util;

import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MarkUtils {
    public static ItemStack getReliquaryFromCaster(LivingEntity caster) {
        ItemStack main = caster.getMainHandItem();
        if (main.is(AddonItemRegistry.UNSTABLE_RELIQUARY.get())) return main;
        ItemStack offhand = caster.getOffhandItem();
        if (offhand.is(AddonItemRegistry.UNSTABLE_RELIQUARY.get())) return offhand;
        return null;
    }

    public static MarkType getMarkType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;

        String markType = tag.getString("mark_type");
        return MarkType.valueOf(markType);
    }
}
