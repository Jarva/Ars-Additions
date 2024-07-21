package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CharmRegistry {
    public enum CharmType implements StringRepresentable {
        FIRE_RESISTANCE(1000, "Emberward", "Nullifies Fire Damage", Items.MAGMA_CREAM, Items.LAVA_BUCKET, Items.FLINT_AND_STEEL),
        UNDYING(1, "Second Wind", "Prevents you from dying", Items.TOTEM_OF_UNDYING, Items.CRYING_OBSIDIAN, Items.GLOWSTONE),
        DISPEL_PROTECTION(3, "Unyielding Magic", "Prevents you from being dispelled", Items.MILK_BUCKET, Items.SHIELD, Items.SPIDER_EYE, Items.WITHER_ROSE),
        FALL_PREVENTION(20, "Featherlight", "Enables you to float down like a feather", Items.FEATHER, Items.PHANTOM_MEMBRANE),
        WATER_BREATHING(1000, "Ocean's Breath", "Enables you to breath underwater", Items.PUFFERFISH, Items.INK_SAC, Items.KELP),
        ENDER_MASK(100, "Ender Serenity", "Masks you from an Enderman's anger", Items.PUMPKIN, Items.CHORUS_FRUIT),
        VOID_PROTECTION(1, "Void's Salvation", "Saves you from the void", ItemsRegistry.STABLE_WARP_SCROLL, Items.END_STONE_BRICKS),
        SONIC_BOOM_PROTECTION(3, "Resonant Shield", "Protects you from the Warden's Sonic Boom", Items.SCULK, Items.SHIELD, Items.WHITE_WOOL),
        WITHER_PROTECTION(10, "Decay's End", "Prevents you from being afflicted with Wither", Items.WITHER_ROSE, Items.WITHER_SKELETON_SKULL, Items.MILK_BUCKET),
        GOLDEN(1000, "Gilded Friendship", "Makes Piglins neutral to the wearer", Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GILDED_BLACKSTONE),
        NIGHT_VISION(20, "Darkvision", "Enables the wearer to see in low-light environments", Items.LANTERN, Items.TORCH),
        POWDERED_SNOW_WALK(1000, "Snowstride", "Enables the wearer to walk on powdered snow", Items.LEATHER_BOOTS, Items.POWDER_SNOW_BUCKET);

        private final int charges;
        private final String name;
        private final String desc;
        private final ArrayList<ItemLike> pedestalItems = new ArrayList<>();

        CharmType(int charges, String name, String desc, ItemLike... items) {
            this.charges = charges;
            this.name = name;
            this.desc = desc;
            pedestalItems.addAll(List.of(items));
        }

        public int getCharges() {
            return charges;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return desc;
        }

        public ArrayList<ItemLike> getPedestalItems() {
            return pedestalItems;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase() + "_charm";
        }
    }

    public static boolean isEnabled(CharmType type, LivingEntity entity) {
        return !getCharm(entity, type).isEmpty();
    }

    public static boolean isEnabled(CharmType type, ItemStack charm) {
        return charm.is(AddonItemRegistry.CHARMS.get(type).get()) && isEnabled(charm);
    }

    public static boolean isEnabled(ItemStack charm) {
        return getCharges(charm) > 0;
    }

    public static int getCharges(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getOrCreateTag().getInt("charges");
    }

    public static void setCharges(ItemStack stack, int charges) {
        stack.getOrCreateTag().putInt("charges", Math.max(charges, 0));
    }

    public static ItemStack getCharm(LivingEntity entity, CharmType charm) {
        return PlayerInvUtil.findItem(entity, stack -> isEnabled(charm, stack), ItemStack.EMPTY, Function.identity());
    }

    public static boolean processCharmEvent(LivingEntity entity, CharmType charm, Supplier<Boolean> predicate, BiFunction<LivingEntity, ItemStack, Integer> consumer) {
        if (!predicate.get()) return false;

        ItemStack curio = getCharm(entity, charm);
        if (curio == null || curio.isEmpty()) return false;

        int damage = consumer.apply(entity, curio);
        if (entity instanceof Player player && player.isCreative()) {
            return true;
        }
        if (damage > 0) {
            setCharges(curio, getCharges(curio) - damage);
        }
        return true;
    }

    public static int every(int ticks, LivingEntity entity, int charges) {
        return entity.level().getGameTime() % ticks == 0 ? charges : 0;
    }
}
