package com.github.jarva.arsadditions.mixin.wixie;

import com.github.jarva.arsadditions.common.util.IWixieOutputStorage;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.recipe.CraftingManager;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.api.util.NearbyPlayerCache;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin {
    @WrapOperation(method = "completeCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean onCompleteCraft(Level instance, Entity entity, Operation<Boolean> original, @Local(argsOnly = true) WixieCauldronTile tile) {
        if (tile instanceof IWixieOutputStorage storage && entity instanceof ItemEntity itemEntity) {
            BlockPos pos = storage.ars_additions$getOutputStorage();
            if (pos == null) return original.call(instance, entity);

            ItemStack item = itemEntity.getItem();
            if (item.isEmpty()) return original.call(instance, entity);

            @Nullable IItemHandler capability = instance.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (capability == null) {
                storage.ars_additions$setOutputStorage(null);
                return original.call(instance, entity);
            }

            FilterableItemHandler handler = new FilterableItemHandler(capability, InvUtil.filtersOnTile(tile));
            InventoryManager manager = new InventoryManager(List.of(handler));
            ItemStack rem = manager.insertStack(item);
            if (!rem.isEmpty()) {
                itemEntity.setItem(rem);
                return original.call(instance, entity);
            }

            if (instance instanceof ServerLevel serverLevel) {
                spawnFlyingItem(serverLevel, pos, tile.getBlockPos(), item.copy());
            }
            return true;
        }
        return original.call(instance, entity);
    }

    private void spawnFlyingItem(ServerLevel level, BlockPos to, BlockPos from, ItemStack stack) {
        if (!NearbyPlayerCache.isPlayerNearby(to, level, 64)) return;

        BlockPos above = from.above();
        EntityFlyingItem flyingItem = new EntityFlyingItem(level,
                new Vec3(above.getX() + 0.5, above.getY(), above.getZ() + 0.5).add(ParticleUtil.inRange(-0.25, 0.25), 0, ParticleUtil.inRange(-0.25, 0.25)),
                new Vec3(to.getX() + 0.5, to.getY(), to.getZ() + 0.5).add(ParticleUtil.inRange(-0.25, 0.25), 0, ParticleUtil.inRange(-0.25, 0.25)));
        flyingItem.getEntityData().set(EntityFlyingItem.HELD_ITEM, stack.copy());
        level.addFreshEntity(flyingItem);
    }
}
