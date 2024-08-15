package com.github.jarva.arsadditions.common.item.curios;

import com.github.jarva.arsadditions.common.item.data.WarpBindData;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
        if(stack.has(AddonDataComponentRegistry.WARP_BIND_DATA)) {
            WarpBindData data = stack.get(AddonDataComponentRegistry.WARP_BIND_DATA);
            int x = data.pos().pos().getX();
            int y = data.pos().pos().getY();
            int z = data.pos().pos().getZ();
            String dim = data.pos().dimension().location().toString();
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
            stack.set(AddonDataComponentRegistry.WARP_BIND_DATA, new WarpBindData(new GlobalPos(c.getLevel().dimension(), pos)));
            if(c.getPlayer() != null)
                c.getPlayer().displayClientMessage(Component.translatable("chat.ars_additions.warp_index.bound", LangUtil.storageLectern()), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult activateTerminal(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
        if (!stack.has(AddonDataComponentRegistry.WARP_BIND_DATA)) {
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

        WarpBindData data = stack.get(AddonDataComponentRegistry.WARP_BIND_DATA);
        int x = data.pos().pos().getX();
        int y = data.pos().pos().getY();
        int z = data.pos().pos().getZ();
        ResourceKey<Level> dim = data.pos().dimension();
        Level lecternWorld = worldIn.getServer().getLevel(dim);
        if(lecternWorld.isLoaded(new BlockPos(x, y, z))) {
            BlockHitResult lookingAt = new BlockHitResult(new Vec3(x, y, z), Direction.UP, new BlockPos(x, y, z), true);
            BlockState state = lecternWorld.getBlockState(lookingAt.getBlockPos());
            if(state.is(BlockRegistry.CRAFTING_LECTERN.get())) {
                return state.useItemOn(stack, lecternWorld, playerIn, handIn, lookingAt).result();
            } else {
                playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.invalid_block", LangUtil.storageLectern()), true);
            }
        } else {
            playerIn.displayClientMessage(Component.translatable("chat.ars_additions.warp_index.out_of_range", LangUtil.storageLectern()), true);
        }

        return InteractionResult.PASS;
    }

    public boolean canActivate(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
        return stack.has(AddonDataComponentRegistry.WARP_BIND_DATA) && stack.get(AddonDataComponentRegistry.WARP_BIND_DATA).pos().dimension().equals(worldIn.dimension());
    }

    public void open(Player sender, ItemStack t) {
        activateTerminal(sender.level(), t, sender, InteractionHand.OFF_HAND);
    }
}
