package com.github.jarva.arsadditions.common.item.data.mark;

import com.github.jarva.arsadditions.setup.registry.MarkDataRegistry;
import com.hollingsworth.arsnouveau.api.spell.CastResolveType;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MarkData {
    Codec<MarkData> CODEC = MarkDataRegistry.MARK_DATA_DISPATCHER.dispatchedCodec();
    StreamCodec<RegistryFriendlyByteBuf, MarkData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC).cast();

    default void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

    }

    default void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

    }

    default CastResolveType cast(SpellContext context, ItemStack reliquary, ServerLevel level, LivingEntity caster, SpellResolver resolver) {
        return CastResolveType.FAILURE;
    }

    default int damageAmount(ItemStack stack, LivingEntity entity, @Nullable Entity target) {
        return 0;
    }

    MapCodec<? extends MarkData> codec();
}
