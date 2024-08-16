package com.github.jarva.arsadditions.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
//    @Inject(method = "addEntitiesToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;createEntityIgnoreException(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;", shift = At.Shift.BEFORE))
//    private void updatePaintingPosition(ServerLevelAccessor arg, BlockPos arg2, StructurePlaceSettings placementIn, CallbackInfo ci, @Local StructureTemplate.StructureEntityInfo info, @Local CompoundTag compoundTag) {
//        if (compoundTag.getString("id").equals("minecraft:painting") && compoundTag.getString("variant").equals("ars_additions:snoozebuncle")) {
//            compoundTag.putInt("TileX", info.blockPos.getX());
//            compoundTag.putInt("TileY", info.blockPos.getY());
//            compoundTag.putInt("TileZ", info.blockPos.getZ());
//        }
//    }
//
//    @WrapOperation(method = "lambda$addEntitiesToWorld$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveTo(DDDFF)V"))
//    private static void wrapMoveTo(Entity instance, double x, double y, double z, float yRot, float xRot, Operation<Void> original, @Local CompoundTag tag) {
//        if (tag.getString("id").equals("minecraft:painting") && tag.getString("variant").equals("ars_additions:snoozebuncle")) {
//            int tX = tag.getInt("TileX");
//            int tY = tag.getInt("TileY");
//            int tZ = tag.getInt("TileZ");
//            instance.moveTo(tX, tY, tZ, yRot, xRot);
//        } else {
//            instance.moveTo(x, y, z, yRot, xRot);
//        }
//    }
}
