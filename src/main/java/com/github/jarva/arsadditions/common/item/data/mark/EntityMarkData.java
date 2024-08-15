package com.github.jarva.arsadditions.common.item.data.mark;

import com.github.jarva.arsadditions.common.item.UnstableReliquary;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.setup.registry.AddonEffectRegistry;
import com.hollingsworth.arsnouveau.api.spell.CastResolveType;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
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
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EntityMarkData(UUID uuid, Holder<EntityType<?>> entityType, Optional<Component> entityName) implements MarkData {
    public static final MapCodec<EntityMarkData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            UUIDUtil.CODEC.fieldOf("entity_uuid").forGetter(EntityMarkData::uuid),
            BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("entity_type").forGetter(EntityMarkData::entityType),
            ComponentSerialization.CODEC.optionalFieldOf("entity_name").forGetter(EntityMarkData::entityName)
    ).apply(inst, EntityMarkData::new));

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level instanceof ServerLevel serverLevel) {
            Entity found = serverLevel.getEntity(uuid);
            if (found == null || !found.isAlive()) {
                UnstableReliquary.breakReliquary(stack);
                return;
            }
            if (found instanceof ServerPlayer player && !player.hasEffect(AddonEffectRegistry.MARKED_EFFECT)) {
                UnstableReliquary.breakReliquary(stack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (entityType.isBound()) {
            EntityType<?> type = entityType.value();
            Component marked = Component.translatable("tooltip.ars_additions.reliquary.marked", Component.translatable(type.getDescriptionId()));
            if (entityName.isPresent()) {
                tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked.name", marked, entityName.get()));
            } else {
                tooltipComponents.add(marked);
            }
        }
    }

    @Override
    public CastResolveType cast(SpellContext context, ItemStack reliquary, ServerLevel level, LivingEntity caster, SpellResolver resolver) {
        Entity found = level.getEntity(uuid);
        if (found == null) {
            UnstableReliquary.breakReliquary(reliquary);
            return CastResolveType.FAILURE;
        }

        resolver.onResolveEffect(level, new EntityHitResult(found));
        UnstableReliquary.damage(this, reliquary, caster);
        return CastResolveType.SUCCESS;
    }

    @Override
    public int damageAmount(ItemStack stack, LivingEntity entity, @Nullable Entity target) {
        return target instanceof Player ? ServerConfig.SERVER.reliquary_cost_player.get() : ServerConfig.SERVER.reliquary_cost_entity.get();
    }

    @Override
    public MapCodec<? extends MarkData> codec() {
        return CODEC;
    }
}
