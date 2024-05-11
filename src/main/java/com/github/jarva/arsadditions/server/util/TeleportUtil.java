package com.github.jarva.arsadditions.server.util;

import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

public class TeleportUtil {
    public static void teleport(ServerLevel level, WarpScroll.WarpScrollData data, Player player, ItemStack stack) {
        teleport(level, data, player);
        stack.shrink(1);
    }

    public static void teleport(ServerLevel level, WarpScroll.WarpScrollData data, Player player) {
        if (!data.isValid()) return;

        BlockPos pos = data.getPos();
        player.teleportToWithTicket(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Vec2 rotation = data.getRotation();
        player.setXRot(rotation.x);
        player.setYRot(rotation.y);

        createTeleportDecoration(level, pos);
    }

    public static void createTeleportDecoration(ServerLevel level, BlockPos pos, ItemStack stack) {
        createTeleportDecoration(level, pos);
        stack.shrink(1);
    }

    public static void createTeleportDecoration(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.PORTAL, pos.getX(), pos.getY() + 1.0, pos.getZ(), 10,
                (level.random.nextDouble() - 0.5D) * 2.0D, -level.random.nextDouble(),
                (level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        level.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }
}
