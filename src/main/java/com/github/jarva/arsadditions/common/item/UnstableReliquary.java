package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.setup.registry.AddonEffectRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.server.util.MarkType;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnstableReliquary extends Item {
    public UnstableReliquary() {
        super(new Properties().stacksTo(1).durability(1000));
    }

//    @Override
//    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//        super.initializeClient(consumer);
//        consumer.accept(new IClientItemExtensions() {
//            ReliquaryRenderer renderer = new ReliquaryRenderer();
//            @Override
//            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
//                return renderer;
//            }
//        });
//    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String markType = tag.getString("mark_type");
        MarkType mark = MarkType.valueOfDefaulted(markType);
        CompoundTag data = tag.getCompound("mark_data");
        if (mark == MarkType.ENTITY) {
            if (level instanceof ServerLevel serverLevel) {
                UUID uuid = data.getUUID("entity_uuid");
                Entity found = serverLevel.getEntity(uuid);
                if (found == null || !found.isAlive()) {
                    UnstableReliquary.breakReliquary(stack);
                    return;
                }
                if (found instanceof ServerPlayer player && !player.hasEffect(AddonEffectRegistry.MARKED_EFFECT.get())) {
                    UnstableReliquary.breakReliquary(stack);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        CompoundTag tag = stack.getTag();
        if (tag != null) {

            String markType = tag.getString("mark_type");

            MarkType mark = MarkType.valueOfDefaulted(markType);
            CompoundTag data = tag.getCompound("mark_data");

            if (mark == MarkType.ENTITY) {
                String entityTypeStr = data.getString("entity_type");
                Optional<EntityType<?>> entityType = EntityType.byString(entityTypeStr);
                if (entityType.isPresent()) {
                    EntityType<?> type = entityType.get();
                    Component marked = Component.translatable("tooltip.ars_additions.reliquary.marked", Component.translatable(type.getDescriptionId()));
                    if (data.contains("entity_name")) {
                        Component name = Component.Serializer.fromJson(data.getString("entity_name"));
                        tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.name", marked, name));
                    } else {
                        tooltipComponents.add(marked);
                    }
                    return;
                }
            }

            if (mark == MarkType.LOCATION) {
                BlockPos pos = NbtUtils.readBlockPos(data.getCompound("block_pos"));
                Component loc = Component.translatable("tooltip.ars_additions.reliquary.marked.location", pos.getX(), pos.getY(), pos.getZ());
                tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked", loc));
                return;
            }

            if (mark == MarkType.BROKEN) {
                tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.broken"));
            }
        }

        tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.empty"));
    }

    public static void breakReliquary(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        tag.putString("mark_type", MarkType.BROKEN.toString());
        tag.put("mark_data", new CompoundTag());
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

    public static MarkType getMarkType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;

        String markType = tag.getString("mark_type");
        return MarkType.valueOfDefaulted(markType);
    }

    public static void damage(MarkType type, ItemStack stack, LivingEntity entity) {
        damage(type, stack, entity, null);
    }

    public static void damage(MarkType type, ItemStack stack, LivingEntity entity, @Nullable Entity target) {
        int amount = switch (type) {
            case ENTITY -> target instanceof Player ? ServerConfig.SERVER.reliquary_cost_player.get() : ServerConfig.SERVER.reliquary_cost_entity.get();
            case LOCATION -> ServerConfig.SERVER.reliquary_cost_location.get();
            case EMPTY, BROKEN -> 0;
        };

        stack.hurtAndBreak(amount, entity, (e) -> {

        });
    }
}
