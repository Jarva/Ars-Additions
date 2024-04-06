package com.github.jarva.arsadditions.glyph;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.registry.names.AddonGlyphNames;
import com.github.jarva.arsadditions.util.MarkType;
import com.github.jarva.arsadditions.util.MarkUtils;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class MethodRecall extends AbstractCastMethod {
    public static MethodRecall INSTANCE = new MethodRecall();

    private MethodRecall() {
        super(ArsAdditions.prefix(AddonGlyphNames.MethodRecall), "Recall");
    }

    @Override
    public String getBookDescription() {
        return "Recalls the target stored in a Reliquary and casts the spell on it.";
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        return cast(playerEntity, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Player player = context.getPlayer();
        if (player == null) return CastResolveType.FAILURE;
        return cast(player, resolver);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(caster, resolver);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return cast(caster, resolver);
    }

    private CastResolveType cast(LivingEntity caster, SpellResolver resolver) {
        ItemStack reliquary = MarkUtils.getReliquaryFromCaster(caster);
        if (reliquary == null) return CastResolveType.FAILURE;

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return CastResolveType.FAILURE;

        MarkType mark = MarkUtils.getMarkType(reliquary);
        if (mark == null) return CastResolveType.FAILURE;

        CompoundTag tag = reliquary.getTag();
        CompoundTag data = tag.getCompound("mark_data");

        if (mark == MarkType.ENTITY) {
            UUID uuid = data.getUUID("entity_uuid");
            Entity found = serverLevel.getEntity(uuid);
            if (found == null) return CastResolveType.FAILURE;

            resolver.onResolveEffect(serverLevel, new EntityHitResult(found));
            return CastResolveType.SUCCESS;
        }

        if (mark == MarkType.LOCATION) {
            BlockPos pos = NbtUtils.readBlockPos(data.getCompound("block_pos"));
            String dim = data.getString("block_dimension");
            if (!dim.equals(caster.level().dimension().location().toString())) return CastResolveType.FAILURE;

            BlockHitResult bhr = new BlockHitResult( pos.getCenter(), Direction.UP, pos, false);
            resolver.onResolveEffect(caster.level(), bhr);
            return CastResolveType.SUCCESS;
        }

        return CastResolveType.FAILURE;
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.add(EffectMark.INSTANCE.getRegistryName());
    }

    @Override
    protected int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
