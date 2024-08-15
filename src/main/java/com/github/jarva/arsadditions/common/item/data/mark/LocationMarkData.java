package com.github.jarva.arsadditions.common.item.data.mark;

import com.github.jarva.arsadditions.common.item.UnstableReliquary;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.api.spell.CastResolveType;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record LocationMarkData(GlobalPos pos) implements MarkData {
    public static final MapCodec<LocationMarkData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            GlobalPos.CODEC.fieldOf("pos").forGetter(LocationMarkData::pos)
    ).apply(inst, LocationMarkData::new));

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Component loc = Component.translatable("tooltip.ars_additions.reliquary.marked.location", pos.pos().getX(), pos.pos().getY(), pos.pos().getZ());
        tooltipComponents.add(Component.translatable("tooltip.ars_additions.reliquary.marked", loc));
    }

    @Override
    public CastResolveType cast(SpellContext context, ItemStack reliquary, ServerLevel level, LivingEntity caster, SpellResolver resolver) {
        if (!pos.dimension().equals(caster.level().dimension().location().toString())) return CastResolveType.FAILURE;

        BlockHitResult bhr = new BlockHitResult(pos.pos().getCenter(), Direction.UP, pos.pos(), false);
        resolver.onResolveEffect(caster.level(), bhr);
        UnstableReliquary.damage(this, reliquary, caster);
        return CastResolveType.SUCCESS;
    }

    @Override
    public int damageAmount(ItemStack stack, LivingEntity entity, @Nullable Entity target) {
        return ServerConfig.SERVER.reliquary_cost_location.get();
    }

    @Override
    public MapCodec<? extends MarkData> codec() {
        return CODEC;
    }
}
