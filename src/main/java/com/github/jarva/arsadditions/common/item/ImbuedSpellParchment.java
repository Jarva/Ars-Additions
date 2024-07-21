package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;

public class ImbuedSpellParchment extends SpellParchment {
    public ImbuedSpellParchment() {
        super(AddonItemRegistry.defaultItemProperties());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return getSpellCaster(stack).getSpell().getCost();
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ISpellCaster caster = getSpellCaster(stack);
        Spell spell = caster.getSpell();

        if (level instanceof ServerLevel serverLevel) {
            FakePlayer player = ANFakePlayer.getPlayer(serverLevel);
            player.setPos(livingEntity.getPosition(1.0f));
            player.setXRot(livingEntity.getXRot());
            player.setYRot(livingEntity.getYRot());
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, spell, player, LivingCaster.from(livingEntity)));
            if (resolver.onCast(stack, level) && !(livingEntity instanceof Player p && p.isCreative())) {
                stack.shrink(1);
            }
        }

        return stack;
    }
}
