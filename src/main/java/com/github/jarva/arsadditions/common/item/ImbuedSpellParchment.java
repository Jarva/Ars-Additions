package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ImbuedSpellParchment extends SpellParchment {
    public ImbuedSpellParchment() {
        super(AddonItemRegistry.defaultItemProperties().component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        int cost = getSpellCaster(stack).getSpell().getCost();
        int seconds = -Math.floorDiv(-cost, 100);
        return seconds * 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        ItemStack stack = player.getItemInHand(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration > 1) return;

        AbstractCaster<?> caster = getSpellCaster(stack);
        InteractionResultHolder<ItemStack> result = caster.castSpell(level, livingEntity, InteractionHand.MAIN_HAND, Component.translatable("ars_nouveau.invalid_spell"));
        if (result.getResult().consumesAction()) {
            stack.consume(1, livingEntity);
        }

        livingEntity.stopUsingItem();
    }

    @SubscribeEvent
    public static void calcSpellCost(SpellCostCalcEvent event) {
        if (event.context.getCasterTool().getItem() instanceof ImbuedSpellParchment) {
            event.currentCost = 0;
        }
    }
}
