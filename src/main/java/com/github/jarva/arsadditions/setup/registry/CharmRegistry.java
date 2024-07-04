package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CharmRegistry {
    public enum CharmType implements StringRepresentable {
        FIRE_RESISTANCE(1000, "Emberward", "Nullifies Fire Damage", Items.MAGMA_CREAM),
        UNDYING(1, "Second Wind", "Prevents you from dying", Items.TOTEM_OF_UNDYING),
        DISPEL_PROTECTION(3, "Unyielding Magic", "Prevents you from being dispelled", Items.MILK_BUCKET),
        FALL_PREVENTION(3, "Featherlight", "Nullifies Fall Damage", Items.FEATHER),
        WATER_BREATHING(1000, "Ocean's Breath", "Enables you to breath underwater", Items.PUFFERFISH),
        ENDER_MASK(100, "Ender Serenity", "Masks you from an Enderman's anger", Items.PUMPKIN),
        VOID_PROTECTION(1, "Void's Salvation", "Saves you from the void", Items.ENDER_PEARL),
        SONIC_BOOM_PROTECTION(3, "Resonant Shield", "Protects you from the Warden's Sonic Boom", Items.SCULK),
        WITHER_PROTECTION(1000, "Decay's End", "Prevents you from being affected by Wither", Items.WITHER_ROSE),
        GOLDEN(1000, "Gilded Friendship", "Makes Piglins neutral to the wearer", Items.GOLD_INGOT),
//        KNOCKBACK_PREVENTION(5, "Immovable Resolve", "Prevents the wearer from being affected by knockback"),
        POWDERED_SNOW_WALK(1000, "Snowstride", "Enables the wearer to walk on powdered snow", Items.LEATHER_BOOTS);

        private final int charges;
        private final String name;
        private final String desc;
        private final ArrayList<Item> pedestalItems = new ArrayList<>();

        CharmType(int charges, String name, String desc, Item... items) {
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

        public ArrayList<Item> getPedestalItems() {
            return pedestalItems;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase() + "_charm";
        }
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

    public static boolean processCharmEvent(LivingEntity entity, RegistryObject<Item> charm, Supplier<Boolean> predicate, BiFunction<LivingEntity, ItemStack, Integer> consumer) {
        if (!predicate.get()) return false;

        ItemStack curio = PlayerInvUtil.findItem(entity, stack -> stack.is(charm.get()) && isEnabled(stack), ItemStack.EMPTY, Function.identity());
        if (curio == null || curio.isEmpty()) return false;

        int damage = consumer.apply(entity, curio);
        int charges = getCharges(curio);
        if (entity instanceof Player player && player.isCreative()) {
            return true;
        }
        setCharges(curio, charges - damage);
        return true;
    }
}
