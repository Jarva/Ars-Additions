package com.github.jarva.arsadditions.client.renderers.entity;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicCarpetRenderer extends GeoEntityRenderer<MagicCarpetEntity> {
    private static final float MAX_VISUAL_PITCH = 35.0F;
    private static final float MAX_VISUAL_SIDE_TILT = 18.0F;
    private static final double MODEL_HALF_LENGTH = 1.0D;
    private static final double MODEL_HALF_WIDTH = 0.75D;
    private static final double MIN_GROUND_CLEARANCE = 0.02D;
    private static final int GROUND_SAMPLE_DEPTH = 4;

    public MagicCarpetRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicCarpetModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public void render(MagicCarpetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.05D, 0.0D);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    protected void applyRotations(MagicCarpetEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float interpolatedYaw = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
        super.applyRotations(animatable, poseStack, ageInTicks, interpolatedYaw, partialTick, nativeScale);
        float interpolatedPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
        float pitchTilt = -Mth.clamp(interpolatedPitch, -MAX_VISUAL_PITCH, MAX_VISUAL_PITCH);
        pitchTilt = this.clampPitchAgainstGround(animatable, interpolatedYaw, pitchTilt);
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchTilt));
        float sideTilt = Mth.clamp(animatable.getSideTilt(), -MAX_VISUAL_SIDE_TILT, MAX_VISUAL_SIDE_TILT);
        sideTilt = this.clampSideTiltAgainstGround(animatable, interpolatedYaw, sideTilt);
        poseStack.mulPose(Axis.ZP.rotationDegrees(sideTilt));
    }

    private float clampPitchAgainstGround(MagicCarpetEntity entity, float yawDegrees, float desiredPitchDegrees) {
        if (Mth.equal(desiredPitchDegrees, 0.0F)) {
            return 0.0F;
        }

        double minY = entity.getBoundingBox().minY;
        double minClearance = this.sampleGroundClearance(entity.level(), entity.getX(), entity.getZ(), minY);
        float yawRadians = yawDegrees * Mth.DEG_TO_RAD;
        double forwardX = -Mth.sin(yawRadians) * MODEL_HALF_LENGTH;
        double forwardZ = Mth.cos(yawRadians) * MODEL_HALF_LENGTH;
        minClearance = Math.min(minClearance, this.sampleGroundClearance(entity.level(), entity.getX() + forwardX, entity.getZ() + forwardZ, minY));
        minClearance = Math.min(minClearance, this.sampleGroundClearance(entity.level(), entity.getX() - forwardX, entity.getZ() - forwardZ, minY));
        if (!Double.isFinite(minClearance)) {
            return desiredPitchDegrees;
        }

        float maxPitch = this.maxTiltForClearance(minClearance, MODEL_HALF_LENGTH);
        if (maxPitch <= 0.0F) {
            return 0.0F;
        }
        return Mth.clamp(desiredPitchDegrees, -maxPitch, maxPitch);
    }

    private float clampSideTiltAgainstGround(MagicCarpetEntity entity, float yawDegrees, float desiredSideTiltDegrees) {
        if (Mth.equal(desiredSideTiltDegrees, 0.0F)) {
            return 0.0F;
        }

        double minY = entity.getBoundingBox().minY;
        double minClearance = this.sampleGroundClearance(entity.level(), entity.getX(), entity.getZ(), minY);
        float yawRadians = yawDegrees * Mth.DEG_TO_RAD;
        double sideX = Mth.cos(yawRadians) * MODEL_HALF_WIDTH;
        double sideZ = Mth.sin(yawRadians) * MODEL_HALF_WIDTH;
        minClearance = Math.min(minClearance, this.sampleGroundClearance(entity.level(), entity.getX() + sideX, entity.getZ() + sideZ, minY));
        minClearance = Math.min(minClearance, this.sampleGroundClearance(entity.level(), entity.getX() - sideX, entity.getZ() - sideZ, minY));
        if (!Double.isFinite(minClearance)) {
            return desiredSideTiltDegrees;
        }

        float maxSideTilt = this.maxTiltForClearance(minClearance, MODEL_HALF_WIDTH);
        if (maxSideTilt <= 0.0F) {
            return 0.0F;
        }
        return Mth.clamp(desiredSideTiltDegrees, -maxSideTilt, maxSideTilt);
    }

    private float maxTiltForClearance(double minClearance, double tiltRadius) {
        double clearance = Math.max(0.0D, minClearance - MIN_GROUND_CLEARANCE);
        return (float) Math.toDegrees(Math.asin(Mth.clamp((float) (clearance / tiltRadius), 0.0F, 1.0F)));
    }

    private double sampleGroundClearance(Level level, double x, double z, double minY) {
        int blockX = Mth.floor(x);
        int blockZ = Mth.floor(z);
        int startY = Mth.floor(minY - 1.0E-4D);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = startY; y > startY - GROUND_SAMPLE_DEPTH; y--) {
            pos.set(blockX, y, blockZ);
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            if (shape.isEmpty()) {
                continue;
            }

            double topY = (double) y + shape.max(Direction.Axis.Y);
            if (topY >= minY - 1.0E-4D) {
                return 0.0D;
            }
            return minY - topY;
        }

        return Double.POSITIVE_INFINITY;
    }
}
