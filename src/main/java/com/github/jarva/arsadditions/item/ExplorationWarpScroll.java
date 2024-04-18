package com.github.jarva.arsadditions.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.advancement.Triggers;
import com.github.jarva.arsadditions.loot.functions.ExplorationScrollFunction;
import com.github.jarva.arsadditions.util.KeypressUtil;
import com.github.jarva.arsadditions.util.LocateUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.items.StableWarpScroll;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplorationWarpScroll extends Item {
    public ExplorationWarpScroll() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!(entity.getCommandSenderWorld() instanceof ServerLevel serverLevel))
            return false;

        BlockPos pos = entity.blockPosition();
        boolean isRuinedPortal = serverLevel.structureManager().getStructureWithPieceAt(pos, TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix("ruined_portals"))).isValid();
        if (isRuinedPortal) {
            WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
            if (!data.isValid()) return false;

            String displayName = "Explorer's Warp Portal";
            if (BlockRegistry.PORTAL_BLOCK.get().trySpawnPortal(serverLevel, pos, data, displayName)) {
                ANCriteriaTriggers.rewardNearbyPlayers(Triggers.FIND_RUINED_PORTAL, serverLevel, pos, 10);
                ANCriteriaTriggers.rewardNearbyPlayers(Triggers.CREATE_RUINED_PORTAL, serverLevel, pos, 10);
                createTeleportDecoration(stack, serverLevel, pos);
                return true;
            }
        }

        return false;
    }

    private void createTeleportDecoration(ItemStack stack, ServerLevel serverLevel, BlockPos pos) {
        serverLevel.sendParticles(ParticleTypes.PORTAL, pos.getX(), pos.getY() + 1.0, pos.getZ(), 10, (serverLevel.random.nextDouble() - 0.5D) * 2.0D, -serverLevel.random.nextDouble(), (serverLevel.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        serverLevel.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f);
        stack.shrink(1);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.literal("This ancient warp scroll is filled to the brim with unstable magic."));

        if (KeypressUtil.isShiftPressed()) {
            tooltipComponents.add(Component.translatable("tooltip.ars_additions.exploration_warp_scroll.use", Minecraft.getInstance().options.keyUse.getKey().getDisplayName()));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (usedHand == InteractionHand.OFF_HAND) return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if (!(level instanceof ServerLevel serverLevel)) return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        WarpScroll.WarpScrollData data = StableWarpScroll.StableScrollData.get(stack);

        if (!data.isValid()) {
            CompoundTag tag = stack.getTag();
            HolderSet<Structure> holderSet = LocateUtil.getFromTag(serverLevel, ExplorationScrollFunction.DEFAULT_DESTINATION);
            Vec3 origin = player.getPosition(1.0f);
            int searchRadius = ExplorationScrollFunction.DEFAULT_SEARCH_RADIUS;
            boolean skipKnown = ExplorationScrollFunction.DEFAULT_SKIP_EXISTING;
            if (tag != null) {
                if (tag.contains("resource")) {
                    ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(tag.getString("resource")));
                    holderSet = LocateUtil.getFromResource(serverLevel, key);
                }
                if (tag.contains("tag")) {
                    TagKey<Structure> key = TagKey.create(Registries.STRUCTURE, new ResourceLocation(tag.getString("destination")));
                    holderSet = LocateUtil.getFromTag(serverLevel, key);
                }
                if (tag.contains("origin")) {
                    CompoundTag originTag = tag.getCompound("origin");
                    double x = originTag.getDouble("x");
                    double y = originTag.getDouble("y");
                    double z = originTag.getDouble("z");
                    origin = new Vec3(x, y, z);
                }
                if (tag.contains("search_radius")) {
                    searchRadius = tag.getInt("search_radius");
                }
                if (tag.contains("skip_known")) {
                    skipKnown = tag.getBoolean("skip_known");
                }
            }
            PortUtil.sendMessageNoSpam(player, Component.literal("Finding Structure..."));
            LocateUtil.locate(serverLevel, holderSet, BlockPos.containing(origin), searchRadius, skipKnown, pair -> {
                BlockPos blockPos = pair.getFirst();
                if (blockPos == null) {
                    PortUtil.sendMessageNoSpam(player, Component.literal("Unable to locate structure..."));
                    return;
                }
                WarpScroll.WarpScrollData newData = LocateUtil.setScrollData(serverLevel, stack, blockPos);
                teleport(serverLevel, player, stack, newData);
            });
        } else {
            teleport(serverLevel, player, stack, data);
        }

        return InteractionResultHolder.pass(stack);
    }

    private void teleport(ServerLevel level, Player player, ItemStack stack, WarpScroll.WarpScrollData data) {
        BlockPos pos = data.getPos();
        player.teleportToWithTicket(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Vec2 rotation = data.getRotation();
        player.setXRot(rotation.x);
        player.setYRot(rotation.y);

        createTeleportDecoration(stack, level, pos);
    }
}
