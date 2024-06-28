package com.github.jarva.arsadditions.common.item.curios;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Charm extends ArsNouveauCurio {
    private final int uses;

    public Charm(int uses) {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1).durability(uses));
        this.uses = uses;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.ars_additions.charm.desc").withStyle(ChatFormatting.GRAY));

        int charges = stack.hasTag() ? stack.getTag().getInt("charges") : uses;
        tooltip.add(Component.translatable("tooltip.ars_additions.charm.charges", charges, uses).withStyle(ChatFormatting.GOLD));

        String descKey = Util.makeDescriptionId("tooltip", BuiltInRegistries.ITEM.getKey(this));
        tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("charges")) {
            tag.putInt("charges", uses);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return uses;
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        int charges = stack.getOrCreateTag().getInt("charges");
        return uses - charges;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (!stack.hasTag()) return false;
        int charges = stack.getOrCreateTag().getInt("charges");
        return charges != uses;
    }

    public static boolean isEnabled(ItemStack charm) {
        return Charm.getCharges(charm) > 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {}

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return Charm.isEnabled(stack);
    }

    public static int getCharges(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getOrCreateTag().getInt("charges");
    }

    public static void setCharges(ItemStack stack, int charges) {
        stack.getOrCreateTag().putInt("charges", Math.max(charges, 0));
    }

    private static void processCharmEvent(LivingEntity entity, RegistryObject<Item> charm, Supplier<Boolean> predicate, BiFunction<LivingEntity, ItemStack, Integer> consumer) {
        if (!predicate.get()) return;

        ItemStack curio = PlayerInvUtil.findItem(entity, stack -> stack.is(charm.get()) && Charm.isEnabled(stack), ItemStack.EMPTY, Function.identity());
        if (curio == null || curio.isEmpty()) return;

        int damage = consumer.apply(entity, curio);
        int charges = Charm.getCharges(curio);
        if (entity instanceof Player player && player.isCreative()) {
            return;
        }
        Charm.setCharges(curio, charges - damage);
    }

    @SubscribeEvent
    public static void handeUndying(LivingDeathEvent event) {
        processCharmEvent(event.getEntity(), AddonItemRegistry.UNDYING_CHARM, () -> event.getEntity() instanceof Player, (entity, curio) -> {
            entity.setHealth(1.0F);
            entity.removeAllEffects();
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            entity.level().broadcastEntityEvent(entity, (byte)35);
            event.setCanceled(true);

            return 1;
        });
    }

    @SubscribeEvent
    public static void handleDamage(LivingAttackEvent event) {
        processCharmEvent(event.getEntity(), AddonItemRegistry.FALL_PREVENTION_CHARM, () -> event.getSource().is(DamageTypeTags.IS_FALL), (entity, curio) -> {
            event.setCanceled(true);

            return (int) event.getAmount();
        });
        processCharmEvent(event.getEntity(), AddonItemRegistry.FIRE_RESISTANCE_CHARM, () -> event.getSource().is(DamageTypeTags.IS_FIRE), (entity, curio) -> {
            event.setCanceled(true);

            return (int) event.getAmount();
        });
        processCharmEvent(event.getEntity(), AddonItemRegistry.VOID_PROTECTION_CHARM, () -> event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD), (entity, curio) -> {
            event.setCanceled(true);

            if (entity.level() instanceof ServerLevel serverLevel) {
                BlockPos ground = LocateUtil.findHighestSafeBlock(serverLevel, entity.blockPosition());
                if (ground == null) {
                    ground = entity.blockPosition().atY(serverLevel.getMaxBuildHeight());
                }
                TeleportUtil.teleport(serverLevel, ground, entity.getRotationVector(), entity);
            }

            return 1;
        });
    }

    @SubscribeEvent
    public static void handeDispel(DispelEvent.Pre event) {
        if (event.rayTraceResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            processCharmEvent(livingEntity, AddonItemRegistry.DISPEL_PROTECTION_CHARM, () -> true, (entity, curio) -> {
                event.setCanceled(true);

                return 1;
            });
        }
    }

    @SubscribeEvent
    public static void handleEnderMask(EnderManAngerEvent event) {
        processCharmEvent(event.getPlayer(), AddonItemRegistry.ENDER_MASK_CHARM, () -> {
            Player player = event.getPlayer();
            EnderMan enderMan = event.getEntity();
            Vec3 view = player.getViewVector(1.0F).normalize();
            Vec3 vec = new Vec3(enderMan.getX() - player.getX(), enderMan.getEyeY() - player.getEyeY(), enderMan.getZ() - player.getZ());
            double d0 = vec.length();
            vec = vec.normalize();
            double d1 = view.dot(vec);
            return d1 > 1.0 - 0.025 / d0 && player.hasLineOfSight(enderMan);
        }, (entity, curio) -> {
            event.setCanceled(true);

            return 1;
        });
    }
}
