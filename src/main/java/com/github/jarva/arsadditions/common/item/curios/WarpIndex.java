package com.github.jarva.arsadditions.common.item.curios;

import com.github.jarva.arsadditions.common.item.data.WarpBindData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WarpIndex extends Item {
    public WarpIndex() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        WarpBindData data = stack.get(AddonDataComponentRegistry.WARP_BIND_DATA);
        if(data != null) {
            int x = data.x();
            int y = data.y();
            int z = data.z();
            String dim = data.getDimensionString();
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", x, y, z, dim));
        } else {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.storageLectern()));
        }

        tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.keybind", Component.translatable("tooltip.ars_additions.warp_index.keybind.outline", Component.keybind("key.ars_additions.open_lectern")).withStyle(ChatFormatting.GREEN)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        return new InteractionResultHolder<>(activateTerminal(worldIn, playerIn.getItemInHand(handIn), playerIn, handIn), playerIn.getItemInHand(handIn));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext c) {
        if (!c.isSecondaryUseActive()) return InteractionResult.PASS;
        if (c.getLevel().isClientSide()) return InteractionResult.CONSUME;

        BlockPos pos = c.getClickedPos();
        BlockState state = c.getLevel().getBlockState(pos);
        if(state.is(BlockRegistry.CRAFTING_LECTERN.get())) {
            ItemStack stack = c.getItemInHand();
            new WarpBindData(c.getLevel(), pos).write(stack);
            if(c.getPlayer() != null)
                c.getPlayer().displayClientMessage(Component.translatable("chat.ars_additions.warp_index.bound", LangUtil.storageLectern()), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult activateTerminal(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
        WarpBindData data = stack.get(AddonDataComponentRegistry.WARP_BIND_DATA);
        if (data == null) {
            playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.storageLectern()), true);
            return InteractionResult.PASS;
        }
        if (!canActivate(worldIn, stack, playerIn, handIn)) {
            playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.no_activate", LangUtil.storageLectern()), true);
            return InteractionResult.PASS;
        }
        if (worldIn.isClientSide) {
            return InteractionResult.CONSUME;
        }

        MinecraftServer server = worldIn.getServer();
        if (server == null) {
            return InteractionResult.CONSUME;
        }

        ResourceKey<Level> dim = data.dimension();
        BlockPos boundPos = data.blockPos();
        Level lecternWorld = server.getLevel(dim);
        // TODO: handle when stored level is missing from server with custom error
        if (lecternWorld == null || !lecternWorld.isLoaded(boundPos)) {
            playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.out_of_range", LangUtil.storageLectern()), true);
            return InteractionResult.FAIL;
        }

        BlockState state = lecternWorld.getBlockState(boundPos);
        if(!state.is(BlockRegistry.CRAFTING_LECTERN.get())) {
            playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.invalid_block", LangUtil.storageLectern()), true);
            return InteractionResult.FAIL;
        }

        BlockHitResult lookingAt = new BlockHitResult(Vec3.atLowerCornerOf(boundPos), Direction.UP, boundPos, true);
        return state.useItemOn(stack, lecternWorld, playerIn, handIn, lookingAt).result();

    }

    public boolean canActivate(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
        WarpBindData data = stack.get(AddonDataComponentRegistry.WARP_BIND_DATA);
        if (data == null) return false;
        return data.isIn(worldIn.dimension());
    }

    public void open(Player sender, ItemStack t) {
        activateTerminal(sender.level(), t, sender, InteractionHand.MAIN_HAND);
    }
}
