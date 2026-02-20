package com.github.jarva.arsadditions.common.commands;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ArsCarpetCommand {
    private static final double SUMMON_SEARCH_RADIUS = 96.0D;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-carpet")
                .executes(ArsCarpetCommand::summonNearestCarpet)
                .then(Commands.literal("summon")
                        .executes(ArsCarpetCommand::summonNearestCarpet)
                )
        );
    }

    private static int summonNearestCarpet(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Player required when running from console."));
            return 0;
        }

        MagicCarpetEntity carpet = findNearestOwnedCarpet(player);
        if (carpet == null) {
            source.sendFailure(Component.translatable("chat.ars_additions.magic_carpet.not_found"));
            return 0;
        }

        if (!carpet.summonTo(player)) {
            source.sendFailure(Component.translatable("chat.ars_additions.magic_carpet.busy"));
            return 0;
        }

        source.sendSuccess(() -> Component.translatable("chat.ars_additions.magic_carpet.coming"), false);
        return 1;
    }

    private static MagicCarpetEntity findNearestOwnedCarpet(ServerPlayer player) {
        List<MagicCarpetEntity> carpets = player.serverLevel().getEntitiesOfClass(
                MagicCarpetEntity.class,
                player.getBoundingBox().inflate(SUMMON_SEARCH_RADIUS),
                carpet -> carpet.isAlive() && carpet.isOwnedBy(player)
        );

        MagicCarpetEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MagicCarpetEntity carpet : carpets) {
            double distance = carpet.distanceToSqr(player);
            if (distance < nearestDistance) {
                nearest = carpet;
                nearestDistance = distance;
            }
        }
        return nearest;
    }
}
