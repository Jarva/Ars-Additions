package com.github.jarva.arsadditions.common.commands;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class SetLootTableCommand {
    private static final SuggestionProvider<CommandSourceStack> sugg = (ctx, builder) -> SharedSuggestionProvider.suggestResource(GlyphRegistry.getSpellpartMap().keySet(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctxt) {
        dispatcher.register(Commands.literal("set_loot_tables")
                .requires(sender -> sender.hasPermission(4))
                .then(
                        Commands.argument("filter", BlockStateArgument.block(ctxt))
                                .then(
                                        Commands.argument("from", BlockPosArgument.blockPos())
                                                .then(
                                                        Commands.argument("to", BlockPosArgument.blockPos())
                                                                .then(
                                                                        Commands.argument("loot", ResourceLocationArgument.id()).suggests(LootCommand.SUGGEST_LOOT_TABLE)
                                                                                .executes(context ->
                                                                                        replaceLootTables(context, BlockStateArgument.getBlock(context, "filter"), BlockPosArgument.getBlockPos(context, "from"), BlockPosArgument.getBlockPos(context, "to"), ResourceLocationArgument.getId(context, "loot"))
                                                                                )
                                                                )
                                                )
                                )
                )
        );
    }

    private static int replaceLootTables(CommandContext<CommandSourceStack> context, BlockInput filter, BlockPos from, BlockPos to, ResourceLocation loot) {
        ServerLevel level = context.getSource().getLevel();

        int counter = 0;
        for (BlockPos pos : BlockPos.betweenClosed(from, to)) {
            if (!filter.test(new BlockInWorld(level, pos, true))) continue;
            BlockState bs = level.getBlockState(pos);
            BlockEntity be = level.getBlockEntity(pos);

            if (be == null) continue;

            CompoundTag tag = be.saveWithFullMetadata(level.registryAccess());
            tag.putString("LootTable", loot.toString());
            be.loadCustomOnly(tag, level.registryAccess());
            be.setChanged();
            be.getLevel().sendBlockUpdated(pos, bs, bs, 3);
            counter++;
        }

        if (context.getSource().getPlayer() != null) {
            Player player = context.getSource().getPlayer();
            player.sendSystemMessage(Component.literal("Updated " + counter + " loot tables"));
        }

        return 1;
    }
}
