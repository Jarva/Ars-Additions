package com.github.jarva.arsadditions.common.item.curios;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.CharmData;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.CharmRegistry;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = ArsAdditions.MODID, bus = EventBusSubscriber.Bus.GAME)
public class Charm extends ArsNouveauCurio {

    private final int uses;

    public Charm(int uses) {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1).durability(uses));
        this.uses = uses;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);

        tooltip2.add(Component.translatable("tooltip.ars_additions.charm.desc").withStyle(ChatFormatting.GRAY));

        int charges = CharmData.getOrDefault(stack, uses).charges();
        tooltip2.add(Component.translatable("tooltip.ars_additions.charm.charges", charges, uses).withStyle(ChatFormatting.GOLD));

        String descKey = Util.makeDescriptionId("tooltip", BuiltInRegistries.ITEM.getKey(this));
        tooltip2.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return uses;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return uses - CharmData.getOrDefault(stack, uses).charges();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        int charges = CharmData.getOrDefault(stack, uses).charges();
        return charges != uses;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return super.canElytraFly(stack, entity);
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
        return CharmRegistry.isEnabled(stack);
    }

    @Override
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return isEnderMask(player, endermanEntity);
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        return isEnderMask(slotContext.entity(), enderMan);
    }

    public boolean isEnderMask(LivingEntity entity, EnderMan enderMan) {
        return CharmRegistry.processCharmEvent(entity, CharmRegistry.CharmType.ENDER_MASK, () -> {
            Vec3 view = entity.getViewVector(1.0F).normalize();
            Vec3 vec = new Vec3(enderMan.getX() - entity.getX(), enderMan.getEyeY() - entity.getEyeY(), enderMan.getZ() - entity.getZ());
            double d0 = vec.length();
            vec = vec.normalize();
            double d1 = view.dot(vec);
            return d1 > 1.0 - 0.025 / d0 && entity.hasLineOfSight(enderMan);
        }, (e, curio) -> CharmRegistry.every(10, entity, 1));
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        return makesPiglinsNeutral(slotContext.entity());
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return makesPiglinsNeutral(wearer);
    }

    public boolean makesPiglinsNeutral(LivingEntity wearer) {
        return CharmRegistry.isEnabled(CharmRegistry.CharmType.GOLDEN, wearer);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return canWalkOnPowderedSnow(wearer);
    }

    @Override
    public boolean canWalkOnPowderedSnow(SlotContext slotContext, ItemStack stack) {
        return canWalkOnPowderedSnow(slotContext.entity());
    }

    public boolean canWalkOnPowderedSnow(LivingEntity wearer) {
        return CharmRegistry.processCharmEvent(wearer, CharmRegistry.CharmType.POWDERED_SNOW_WALK, () -> true, (entity, curio) -> CharmRegistry.every(10, entity, 1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity livingEntity) {
            tick(stack, livingEntity);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        tick(stack, slotContext.entity());
    }

    public void tick(ItemStack stack, LivingEntity entity) {
        CharmRegistry.processCharmEvent(entity, CharmRegistry.CharmType.FALL_PREVENTION, () -> entity.fallDistance > 3.0f, (e, curio) -> {
            e.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
            return 1;
        });

        CharmRegistry.processCharmEvent(entity, CharmRegistry.CharmType.NIGHT_VISION, () -> {
            int brightness = entity.level().getRawBrightness(entity.blockPosition(), entity.level().getSkyDarken());
            MobEffectInstance nightvision = entity.getEffect(MobEffects.NIGHT_VISION);
            return brightness < 5 && (nightvision == null || nightvision.getDuration() <= (10 * 20));
        }, (e, curio) -> {
            e.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 30));
            return 1;
        });

        CharmRegistry.processCharmEvent(entity, CharmRegistry.CharmType.VOID_PROTECTION, () -> {
            if (!entity.onGround()) return false;
            BlockPos below = entity.blockPosition().below();
            return entity.level().getBlockState(below).isRedstoneConductor(entity.level(), below);
        }, (e, curio) -> {
            GlobalPos pos = GlobalPos.of(e.level().dimension(), e.blockPosition());
            LodestoneTracker tracker = new LodestoneTracker(Optional.of(pos), false);
            curio.set(DataComponents.LODESTONE_TRACKER, tracker);
            return 0;
        });
    }

    @SubscribeEvent
    public static void handeUndying(LivingDeathEvent event) {
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.UNDYING, () -> event.getEntity() instanceof Player, (entity, curio) -> {
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
    public static void handleDamage(LivingDamageEvent.Pre event) {
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.FIRE_RESISTANCE, () -> event.getSource().is(DamageTypeTags.IS_FIRE), (entity, curio) -> {
            event.setNewDamage(0);

            if (event.getSource().is(DamageTypes.LAVA)) {
                return CharmRegistry.every(10, entity, (int) event.getOriginalDamage());
            }

            return (int) event.getOriginalDamage();
        });
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.WATER_BREATHING, () -> event.getSource().is(DamageTypeTags.IS_DROWNING), (entity, curio) -> {
            event.setNewDamage(0);

            return (int) event.getOriginalDamage();
        });
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.VOID_PROTECTION, () -> event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD), (entity, curio) -> {
            event.setNewDamage(0);

            if (entity.level() instanceof ServerLevel serverLevel) {
                LodestoneTracker tracker = curio.get(DataComponents.LODESTONE_TRACKER);
                if (tracker != null) {
                    tracker.target().ifPresent(pos -> {
                        TeleportUtil.teleport(serverLevel.getServer().getLevel(pos.dimension()), pos.pos(), entity.getRotationVector(), entity);
                        entity.resetFallDistance();
                    });
                }
            }

            return 1;
        });
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.SONIC_BOOM_PROTECTION, () -> event.getSource().is(DamageTypes.SONIC_BOOM), (entity, curio) -> {
            event.setNewDamage(0);

            return 1;
        });
    }

    @SubscribeEvent
    public static void denyMobEffects(MobEffectEvent.Applicable event) {
        CharmRegistry.processCharmEvent(event.getEntity(), CharmRegistry.CharmType.WITHER_PROTECTION, () -> event.getEffectInstance().getEffect().equals(MobEffects.WITHER), (entity, curio) -> {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return CharmRegistry.every(10, entity, 1);
        });
    }

    @SubscribeEvent
    public static void handeDispel(DispelEvent.Pre event) {
        if (event.rayTraceResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            CharmRegistry.processCharmEvent(livingEntity, CharmRegistry.CharmType.DISPEL_PROTECTION, () -> !event.shooter.equals(livingEntity), (entity, curio) -> {
                event.setCanceled(true);

                return 1;
            });
        }
    }
}
