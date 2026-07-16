package com.github.jarva.arsadditions.mixin;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MagicCarpetDismountMixin {
    private static final String CARPET_SHIFT_PREV_DOWN_TAG = "ars_additions_magic_carpet_shift_prev_down";
    private static final String CARPET_SHIFT_LAST_TAP_TICK_TAG = "ars_additions_magic_carpet_shift_last_tap_tick";
    private static final int CARPET_DOUBLE_TAP_WINDOW_TICKS = 7;

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    private void ars_additions$doubleTapDismount(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        boolean clientSide = player.level().isClientSide();

        CompoundTag persistentData = player.getPersistentData();

        if (!(player.getVehicle() instanceof MagicCarpetEntity carpet)) {
            clearDismountState(persistentData);
            return;
        }

        if (!requiresDoubleTapDismount(carpet)) {
            clearDismountState(persistentData);
            return;
        }

        boolean shiftDown = player.isShiftKeyDown();
        boolean previousShiftDown = persistentData.getBoolean(CARPET_SHIFT_PREV_DOWN_TAG);

        if (!shiftDown) {
            persistentData.putBoolean(CARPET_SHIFT_PREV_DOWN_TAG, false);
            return;
        }

        if (previousShiftDown) {
            cir.setReturnValue(false);
            return;
        }

        persistentData.putBoolean(CARPET_SHIFT_PREV_DOWN_TAG, true);

        long currentTick = player.level().getGameTime();
        boolean hasLastTapTick = persistentData.contains(CARPET_SHIFT_LAST_TAP_TICK_TAG);
        long lastTapTick = persistentData.getLong(CARPET_SHIFT_LAST_TAP_TICK_TAG);
        boolean doubleTap = hasLastTapTick && currentTick - lastTapTick <= CARPET_DOUBLE_TAP_WINDOW_TICKS;

        if (doubleTap) {
            clearDismountState(persistentData);
            cir.setReturnValue(true);
            return;
        }

        persistentData.putLong(CARPET_SHIFT_LAST_TAP_TICK_TAG, currentTick);
        if (!clientSide) {
            player.displayClientMessage(
                    Component.translatable("chat.ars_additions.magic_carpet.dismount_confirm", Component.keybind("key.sneak")),
                    true
            );
        }
        cir.setReturnValue(false);
    }

    private static void clearDismountState(CompoundTag persistentData) {
        persistentData.remove(CARPET_SHIFT_PREV_DOWN_TAG);
        persistentData.remove(CARPET_SHIFT_LAST_TAP_TICK_TAG);
    }

    private static boolean requiresDoubleTapDismount(MagicCarpetEntity carpet) {
        return !carpet.onGround();
    }
}
