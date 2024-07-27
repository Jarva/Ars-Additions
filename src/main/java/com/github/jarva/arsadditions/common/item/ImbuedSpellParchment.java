package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ImbuedSpellParchment extends SpellParchment {
    public ImbuedSpellParchment() {
        super(AddonItemRegistry.defaultItemProperties());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        int cost = getSpellCaster(stack).getSpell().getCost();
        int seconds = -Math.floorDiv(-cost, 100);
        return seconds * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        ItemStack stack = player.getItemInHand(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration > 1) return;

        ISpellCaster caster = getSpellCaster(stack);
        InteractionResultHolder<ItemStack> result = caster.castSpell(level, livingEntity, InteractionHand.MAIN_HAND, Component.translatable("ars_nouveau.invalid_spell"));
        if (result.getResult().consumesAction() && !(livingEntity instanceof Player p && p.isCreative())) {
            stack.shrink(1);
        }

        livingEntity.stopUsingItem();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @SubscribeEvent
    public static void calcSpellCost(SpellCostCalcEvent event) {
        LivingEntity entity = event.context.getUnwrappedCaster();
        ItemStack scroll = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (scroll.getItem() instanceof ImbuedSpellParchment imbuedSpellParchment) {
            ISpellCaster caster = imbuedSpellParchment.getSpellCaster(scroll);
            if (caster.getSpell().serialize().equals(event.context.getSpell().serialize())) {
                event.currentCost = 0;
            }
        }
    }
}
