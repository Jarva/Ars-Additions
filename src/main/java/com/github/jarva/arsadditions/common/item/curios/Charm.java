package com.github.jarva.arsadditions.common.item.curios;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import com.github.jarva.arsadditions.server.util.TeleportUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.StringRepresentable;
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
import top.theillusivec4.curios.api.SlotContext;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Charm extends ArsNouveauCurio {
    public enum CharmType implements StringRepresentable {
        FIRE_RESISTANCE,
        UNDYING,
        DISPEL_PROTECTION,
        FALL_PREVENTION,
        WATER_BREATHING,
        ENDER_MASK,
        VOID_PROTECTION,
        SONIC_BOOM_PROTECTION;

        @Override
        public String getSerializedName() {
            return name().toLowerCase() + "_charm";
        }
    }
    public static final HashMap<CharmType, Integer> CHARMS = new HashMap<>();
    static {
        CHARMS.put(CharmType.FIRE_RESISTANCE, 1000);
        CHARMS.put(CharmType.UNDYING, 1);
        CHARMS.put(CharmType.DISPEL_PROTECTION, 3);
        CHARMS.put(CharmType.FALL_PREVENTION, 3);
        CHARMS.put(CharmType.WATER_BREATHING, 1000);
        CHARMS.put(CharmType.ENDER_MASK, 100);
        CHARMS.put(CharmType.VOID_PROTECTION, 1);
        CHARMS.put(CharmType.SONIC_BOOM_PROTECTION, 3);
    }

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

    public static boolean isEnabled(CharmType type, ItemStack charm) {
        return charm.is(AddonItemRegistry.CHARMS.get(type).get()) && isEnabled(charm);
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
        if (Charm.isEnabled(CharmType.VOID_PROTECTION, stack) && entity.onGround()) {
            BlockPos below = entity.blockPosition().below();
            boolean isSafe = entity.level().getBlockState(below).isRedstoneConductor(entity.level(), below);
            if (!isSafe) return;
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, GlobalPos.of(entity.level().dimension(), entity.blockPosition())).result().ifPresent(pos -> {
                stack.getTag().put("Pos", pos);
            });
        }
        processCharmEvent(entity, AddonItemRegistry.CHARMS.get(CharmType.FALL_PREVENTION), () -> {
            CompoundTag tag = stack.getOrCreateTag();
            boolean canTrigger = !tag.contains("canTrigger") || tag.getBoolean("canTrigger");
            if (!canTrigger && entity.onGround()) {
                tag.putBoolean("canTrigger", true);
                return false;
            }
            return canTrigger && entity.fallDistance > 3.0f;
        }, (e, curio) -> {
            e.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100));
            return 1;
        });
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
        processCharmEvent(event.getEntity(), AddonItemRegistry.CHARMS.get(CharmType.UNDYING), () -> event.getEntity() instanceof Player, (entity, curio) -> {
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
        processCharmEvent(event.getEntity(), AddonItemRegistry.CHARMS.get(CharmType.FIRE_RESISTANCE), () -> event.getSource().is(DamageTypeTags.IS_FIRE), (entity, curio) -> {
            event.setCanceled(true);

            return (int) event.getAmount();
        });
        processCharmEvent(event.getEntity(), AddonItemRegistry.CHARMS.get(CharmType.WATER_BREATHING), () -> event.getSource().is(DamageTypeTags.IS_DROWNING), (entity, curio) -> {
            event.setCanceled(true);

            return (int) event.getAmount();
        });
        processCharmEvent(event.getEntity(), AddonItemRegistry.CHARMS.get(CharmType.VOID_PROTECTION), () -> event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD), (entity, curio) -> {
            event.setCanceled(true);

            if (entity.level() instanceof ServerLevel serverLevel) {
                CompoundTag tag = curio.getTag();
                GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Pos")).result().ifPresent(pos -> {
                    TeleportUtil.teleport(serverLevel.getServer().getLevel(pos.dimension()), pos.pos(), entity.getRotationVector(), entity);
                    entity.resetFallDistance();
                });
            }

            return 1;
        });
        processCharmEvent(event.getEntity(), AddonItemRegistry.CHARMS.get(CharmType.SONIC_BOOM_PROTECTION), () -> event.getSource().is(DamageTypes.SONIC_BOOM), (entity, curio) -> {
            event.setCanceled(true);

            return 1;
        });
    }

    @SubscribeEvent
    public static void handeDispel(DispelEvent.Pre event) {
        if (event.rayTraceResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            processCharmEvent(livingEntity, AddonItemRegistry.CHARMS.get(CharmType.DISPEL_PROTECTION), () -> true, (entity, curio) -> {
                event.setCanceled(true);

                return 1;
            });
        }
    }

    @SubscribeEvent
    public static void handleEnderMask(EnderManAngerEvent event) {
        processCharmEvent(event.getPlayer(), AddonItemRegistry.CHARMS.get(CharmType.ENDER_MASK), () -> {
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
