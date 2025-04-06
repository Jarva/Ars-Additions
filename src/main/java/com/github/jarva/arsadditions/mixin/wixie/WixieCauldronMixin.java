package com.github.jarva.arsadditions.mixin.wixie;

import com.github.jarva.arsadditions.common.util.IWixieOutputStorage;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Debug(export = true)
@Mixin(WixieCauldronTile.class)
public abstract class WixieCauldronMixin extends SummoningTile implements IWandable, IWixieOutputStorage {
    @Unique
    private BlockPos ars_additions$finishedStorage;

    public WixieCauldronMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    @Override
    public BlockPos ars_additions$getOutputStorage() {
        return ars_additions$finishedStorage;
    }

    @Override
    public void ars_additions$setOutputStorage(BlockPos pos) {
        ars_additions$finishedStorage = pos;
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level == null) return;

        IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, null);
        if (itemHandler == null) return;

        if (storedPos.equals(ars_additions$finishedStorage)) {
            ars_additions$finishedStorage = null;
            PortUtil.sendMessage(playerEntity, Component.translatable("chat.ars_additions.wixie_cauldron.storage.cleared"));
        } else {
            ars_additions$finishedStorage = storedPos.immutable();
            PortUtil.sendMessage(playerEntity, Component.translatable("chat.ars_additions.wixie_cauldron.storage.set"));
        }
        updateBlock();
    }

    @Inject(method = "onWanded", at = @At(value = "HEAD"))
    private void clearStoragePosition(Player playerEntity, CallbackInfo ci) {
        if (ars_additions$finishedStorage != null) {
            ars_additions$finishedStorage = null;
            PortUtil.sendMessage(playerEntity, Component.translatable("chat.ars_additions.wixie_cauldron.storage.cleared"));
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "TAIL"))
    private void addTooltipLine(List<Component> tooltip, CallbackInfo ci) {
        if (ars_additions$finishedStorage != null) {
            tooltip.add(Component.translatable("tooltip.ars_additions.wixie_cauldron.storage"));
        }
    }

    @Inject(method = "getWandHighlight", at = @At(value = "TAIL"))
    private void addWandHighlight(List<ColorPos> list, CallbackInfoReturnable<List<ColorPos>> cir) {
        if (ars_additions$finishedStorage != null) {
            list.add(ColorPos.centered(ars_additions$finishedStorage, ParticleColor.TO_HIGHLIGHT));
        }
    }

    @Inject(method = "saveAdditional", at = @At(value = "TAIL"))
    private void saveStoragePosition(CompoundTag compound, HolderLookup.Provider pRegistries, CallbackInfo ci) {
        if (ars_additions$finishedStorage != null) {
            compound.put("FinishedStorage", NbtUtils.writeBlockPos(ars_additions$finishedStorage));
        }
    }

    @Inject(method = "loadAdditional", at = @At(value = "TAIL"))
    private void loadStoragePosition(CompoundTag compound, HolderLookup.Provider pRegistries, CallbackInfo ci) {
        ars_additions$finishedStorage = NbtUtils.readBlockPos(compound, "FinishedStorage").orElse(null);
    }
}
