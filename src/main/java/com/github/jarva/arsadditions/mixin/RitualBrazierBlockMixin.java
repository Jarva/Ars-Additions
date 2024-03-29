package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.ritual.RitualChunkLoading;
import com.hollingsworth.arsnouveau.common.block.RitualBrazierBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Debug(export = true)
@Mixin(RitualBrazierBlock.class)
public class RitualBrazierBlockMixin {
    @Inject(method = "playerWillDestroy", at = @At(value = "FIELD", target = "Lcom/hollingsworth/arsnouveau/common/block/tile/RitualBrazierTile;ritual:Lcom/hollingsworth/arsnouveau/api/ritual/AbstractRitual;", shift = At.Shift.BEFORE, ordinal = 0), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void cleanup(Level worldIn, BlockPos pos, BlockState state, Player player, CallbackInfo ci, RitualBrazierTile tile) {
        if (tile.ritual instanceof RitualChunkLoading chunkLoading && chunkLoading.isRunning()) {
            chunkLoading.onEnd();
            ci.cancel();
        }
    }
}
