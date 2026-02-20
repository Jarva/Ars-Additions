package com.github.jarva.arsadditions.common.commands;

import com.github.jarva.arsadditions.server.storage.ChunkLoadingData;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArsChunksCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-chunks")
                .executes(ArsChunksCommand::listSelfChunks)
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> listChunks(ctx, GameProfileArgument.getGameProfiles(ctx, "player")))
                )
                .then(Commands.literal("uuid")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("id", UuidArgument.uuid())
                                .executes(ctx -> listChunksForUuid(ctx.getSource(), UuidArgument.getUuid(ctx, "id")))
                        )
                )
                .then(Commands.literal("all")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> listAllChunks(ctx.getSource()))
                )
        );
    }

    private static int listSelfChunks(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            return listChunksForPlayer(source, player);
        }
        source.sendFailure(Component.literal("Player required when running from console."));
        return 0;
    }

    private static int listChunks(CommandContext<CommandSourceStack> ctx, Collection<GameProfile> targets) {
        CommandSourceStack source = ctx.getSource();
        int count = 0;
        for (GameProfile target : targets) {
            count += listChunksForProfile(source, target);
        }
        return count > 0 ? 1 : 0;
    }

    private static int listChunksForPlayer(CommandSourceStack source, ServerPlayer target) {
        return listChunksForProfile(source, target.getGameProfile());
    }

    private static int listChunksForProfile(CommandSourceStack source, GameProfile profile) {
        UUID uuid = profile.getId();
        if (uuid == null) {
            source.sendFailure(Component.literal("Unknown UUID for player '" + profile.getName() + "'."));
            return 0;
        }
        MinecraftServer server = source.getServer();
        Map<ResourceKey<Level>, Set<ChunkPos>> chunksByDim = ChunkLoadingData.getChunks(server, uuid);

        int total = chunksByDim.values().stream().mapToInt(Set::size).sum();
        if (total == 0) {
            source.sendSuccess(() -> Component.literal("No chunk loading tickets for " + profile.getName() + "."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Chunk loading tickets for " + profile.getName() + " (" + total + " total):"), false);
        for (Map.Entry<ResourceKey<Level>, Set<ChunkPos>> entry : chunksByDim.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            String dimName = entry.getKey().location().toString();
            Set<ChunkPos> chunks = entry.getValue();
            String positions = chunks.stream()
                    .map(pos -> pos.x + "," + pos.z)
                    .collect(Collectors.joining(" | "));
            source.sendSuccess(() -> Component.literal(dimName + " = " + chunks.size() + " chunks"), false);
            source.sendSuccess(() -> Component.literal("Chunks: " + positions), false);
        }

        return 1;
    }

    private static int listChunksForUuid(CommandSourceStack source, UUID uuid) {
        MinecraftServer server = source.getServer();
        Map<ResourceKey<Level>, Set<ChunkPos>> chunksByDim = ChunkLoadingData.getChunks(server, uuid);
        String displayName = resolveDisplayName(server, uuid);

        int total = chunksByDim.values().stream().mapToInt(Set::size).sum();
        if (total == 0) {
            source.sendSuccess(() -> Component.literal("No chunk loading tickets for " + displayName + "."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Chunk loading tickets for " + displayName + " (" + total + " total):"), false);
        for (Map.Entry<ResourceKey<Level>, Set<ChunkPos>> entry : chunksByDim.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            String dimName = entry.getKey().location().toString();
            Set<ChunkPos> chunks = entry.getValue();
            String positions = chunks.stream()
                    .map(pos -> pos.x + "," + pos.z)
                    .collect(Collectors.joining(" | "));
            source.sendSuccess(() -> Component.literal(dimName + " = " + chunks.size() + " chunks"), false);
            source.sendSuccess(() -> Component.literal("Chunks: " + positions), false);
        }

        return 1;
    }

    private static String resolveDisplayName(MinecraftServer server, UUID uuid) {
        ServerPlayer online = server.getPlayerList().getPlayer(uuid);
        if (online != null) {
            return online.getName().getString();
        }
        return server.getProfileCache()
                .get(uuid)
                .map(GameProfile::getName)
                .orElse(uuid.toString());
    }

    private static int listAllChunks(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        Set<UUID> uuids = new HashSet<>();
        for (ServerLevel level : server.getAllLevels()) {
            uuids.addAll(ChunkLoadingData.getData(level).chunks.keySet());
        }

        if (uuids.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No chunk loading tickets found."), false);
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Listing chunk loading tickets for " + uuids.size() + " owner(s):"), false);
        int count = 0;
        for (UUID uuid : uuids) {
            count += listChunksForUuid(source, uuid);
        }
        return count > 0 ? 1 : 0;
    }
}
