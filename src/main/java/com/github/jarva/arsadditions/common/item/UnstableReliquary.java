package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.item.data.mark.BrokenMarkData;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.common.item.data.mark.MarkData;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnstableReliquary extends Item {
    public UnstableReliquary() {
        super(new Properties().stacksTo(1).durability(1000));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!stack.has(AddonDataComponentRegistry.MARK_DATA))
            return;
        stack.get(AddonDataComponentRegistry.MARK_DATA).inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (!stack.has(AddonDataComponentRegistry.MARK_DATA))
            return;
        stack.get(AddonDataComponentRegistry.MARK_DATA).appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static void breakReliquary(ItemStack stack) {
        if (!stack.has(AddonDataComponentRegistry.MARK_DATA))
            return;
        stack.set(AddonDataComponentRegistry.MARK_DATA, BrokenMarkData.INSTANCE);
    }

    public static ItemStack getReliquaryFromCaster(SpellContext context, LivingEntity caster) {
        if (context.getCaster() instanceof TileCaster tileCaster) {
            InventoryManager manager = tileCaster.getInvManager();
            SlotReference reference = manager.findItem(i -> i.is(AddonItemRegistry.UNSTABLE_RELIQUARY.get()), InteractType.EXTRACT);
            if (reference.isEmpty()) return null;

            return reference.getHandler().getStackInSlot(reference.getSlot());
        }

        ItemStack main = caster.getMainHandItem();
        if (main.is(AddonItemRegistry.UNSTABLE_RELIQUARY.get())) return main;
        ItemStack offhand = caster.getOffhandItem();
        if (offhand.is(AddonItemRegistry.UNSTABLE_RELIQUARY.get())) return offhand;
        return null;
    }

    public static void damage(MarkData type, ItemStack stack, LivingEntity entity) {
        damage(type, stack, entity, null);
    }

    public static void damage(MarkData type, ItemStack stack, LivingEntity entity, @Nullable Entity target) {
        if (entity.level().isClientSide())
            return;
        stack.hurtAndBreak(type.damageAmount(stack, entity, target), (ServerLevel) entity.level(), entity, (e) -> {

        });
    }
}
