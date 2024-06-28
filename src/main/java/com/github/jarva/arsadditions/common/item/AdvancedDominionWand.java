package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AdvancedDominionWand extends Item {
    public AdvancedDominionWand() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!(player.level() instanceof ServerLevel serverLevel) || player == null) {
            return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
        }

        if (player.isShiftKeyDown()) {
            AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);
            if (data.getPos() == null && data.getEntityId() == null) {
                data.setData(interactionTarget.getId(), serverLevel.dimension());
                data.write(stack);
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
                return InteractionResult.SUCCESS;
            }

            IWandable wandable = interactionTarget instanceof IWandable wand ? wand : null;
            return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, interactionTarget, null));
        }

        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pPlayer.isShiftKeyDown()) {
            AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);
            data.toggleMode();
            data.write(stack);
            return InteractionResultHolder.success(stack);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
         if (!(context.getLevel() instanceof ServerLevel serverLevel) || context.getPlayer() == null) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();

        if (player.isShiftKeyDown()) {
            BlockPos pos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();

            BlockEntity be = serverLevel.getBlockEntity(pos);

            AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);
            if (data.getPos() == null && data.getEntityId() == null) {
                data.setData(pos, serverLevel.dimension());
                data.write(stack);
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
                return InteractionResult.SUCCESS;
            }

            IWandable wandable = be instanceof IWandable wand ? wand : null;
            return attemptConnection(serverLevel.getServer(), data, player, Triple.of(wandable, null, pos));
        }

        return super.useOn(context);
    }

    private InteractionResult attemptConnection(MinecraftServer server, AdvancedDominionData data, Player player, Triple<IWandable, LivingEntity, BlockPos> target) {
        ServerLevel origin = server.getLevel(data.getLevel());

        IWandable targetWandable = target.getLeft();
        LivingEntity targetLivingEntity = target.getMiddle();
        BlockPos targetBlock = target.getRight();

        Triple<IWandable, LivingEntity, BlockPos> stored = getWandable(origin, data.getPos(), data.getEntityId());

        IWandable storedWandable = stored.getLeft();
        LivingEntity storedLivingEntity = stored.getMiddle();
        BlockPos storedBlock = stored.getRight();

        switch (data.mode) {
            case LOCK_FIRST -> {
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionFirst(targetBlock, null, targetLivingEntity, player);
                }
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionLast(storedBlock, null, storedLivingEntity, player);
                }
            }
            case LOCK_SECOND -> {
                if (storedWandable != null) {
                    storedWandable.onFinishedConnectionLast(targetBlock, null, targetLivingEntity, player);
                }
                if (targetWandable != null) {
                    targetWandable.onFinishedConnectionFirst(storedBlock, null, storedLivingEntity, player);
                }
            }
            default -> {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }

    private Triple<IWandable, LivingEntity, BlockPos> getWandable(ServerLevel level, @Nullable BlockPos pos, @Nullable Integer entityId) {
        if (pos != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IWandable wandable) {
                return Triple.of(wandable, null, pos);
            }
            return Triple.of(null, null, pos);
        }
        if (entityId != null && level.getEntity(entityId) instanceof LivingEntity living) {
            if (living instanceof IWandable wandable) {
                return Triple.of(wandable, living, null);
            }
            return Triple.of(null, living, null);
        }
        return Triple.of(null, null, null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        AdvancedDominionData data = AdvancedDominionData.fromItemStack(stack);

        tooltip.add(Component.translatable("tooltip.ars_additions.advanced_dominion_wand.mode", data.mode.getTranslatable()));

        if (data.getPos() != null) {
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", data.pos.getX(), data.pos.getY(), data.pos.getZ(), data.level.location().toString()));
        } else {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        }
    }

    public static class AdvancedDominionData {

        enum Mode implements StringRepresentable {
            LOCK_FIRST("tooltip.ars_additions.advanced_dominion_wand.mode.first"),
            LOCK_SECOND("tooltip.ars_additions.advanced_dominion_wand.mode.second");

            private final String translatable;

            Mode(String translatable) {
                this.translatable = translatable;
            }

            @Override
            public @NotNull String getSerializedName() {
                return name().toLowerCase();
            }

            public Component getTranslatable() {
                return Component.translatable(translatable);
            }
        }

        public static AdvancedDominionData DEFAULT_DATA = new AdvancedDominionData(Mode.LOCK_FIRST);
        private Mode mode;
        private ResourceKey<Level> level;
        private BlockPos pos;
        private Integer entityId;

        private AdvancedDominionData(Optional<BlockPos> pos, Optional<ResourceKey<Level>> level, Optional<Integer> entityId, Mode mode) {
            this.pos = pos.orElse(null);
            this.level = level.orElse(null);
            this.entityId = entityId.orElse(null);
            this.mode = mode;
        }

        private AdvancedDominionData(Mode mode) {
            this.pos = null;
            this.level = null;
            this.entityId = null;
            this.mode = mode;
        }

        public static final String TAG_KEY = "advanced_dominion";

        public static final Codec<AdvancedDominionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.optionalFieldOf("StoredPos").forGetter(AdvancedDominionData::pos),
                Level.RESOURCE_KEY_CODEC.optionalFieldOf("StoredDim").forGetter(AdvancedDominionData::level),
                Codec.INT.optionalFieldOf("StoredEntity").forGetter(AdvancedDominionData::entityId),
                StringRepresentable.fromEnum(Mode::values).fieldOf("Mode").forGetter(AdvancedDominionData::mode)
        ).apply(instance, AdvancedDominionData::new));

        public static AdvancedDominionData fromItemStack(ItemStack stack) {
            return CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().getCompound(TAG_KEY)).result().orElse(DEFAULT_DATA);
        }

        public void write(ItemStack stack) {
            CODEC.encodeStart(NbtOps.INSTANCE, this).result().ifPresent(tag -> {
                stack.getOrCreateTag().put(TAG_KEY, tag);
            });
        }

        private Optional<BlockPos> pos() {
            return Optional.ofNullable(pos);
        }

        private BlockPos getPos() {
            return pos;
        }

        private Optional<ResourceKey<Level>> level() {
            return Optional.ofNullable(level);
        }

        private ResourceKey<Level> getLevel() {
            return level;
        }

        private Optional<Integer> entityId() {
            return Optional.ofNullable(entityId);
        }

        private Integer getEntityId() {
            return entityId;
        }

        public void setData(BlockPos pos, ResourceKey<Level> level) {
            this.pos = pos;
            this.level = level;
            this.entityId = null;
        }

        public void setData(Integer entityId, ResourceKey<Level> level) {
            this.entityId = entityId;
            this.level = level;
            this.pos = null;
        }

        private Mode mode() {
            return mode;
        }

        public void toggleMode() {
            mode = mode == Mode.LOCK_FIRST ? Mode.LOCK_SECOND : Mode.LOCK_FIRST;
        }
    }
}
