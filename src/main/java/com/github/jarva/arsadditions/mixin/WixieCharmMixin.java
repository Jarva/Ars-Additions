package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.common.block.EnchantingApparatusBlock;
import com.hollingsworth.arsnouveau.common.items.summon_charms.WixieCharm;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WixieCharm.class)
public class WixieCharmMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true, remap = false)
    public void useOnBlock(UseOnContext context, Level world, BlockPos pos, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState blockState = world.getBlockState(pos);
        if (!(context.getPlayer() instanceof ServerPlayer player)) return;
        if (blockState.getBlock() instanceof EnchantingApparatusBlock) {
            world.setBlockAndUpdate(pos, AddonBlockRegistry.WIXIE_ENCHANTING.get().defaultBlockState());
            cir.setReturnValue(InteractionResult.SUCCESS);
            Triggers.WIXIE_ENCHANTING_APPARATUS.get().trigger(player);
        }
    }
}
