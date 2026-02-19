package com.github.jarva.arsadditions.client.renderers.entity;

import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicCarpetRenderer extends GeoEntityRenderer<MagicCarpetEntity> {
    private static final float MODEL_YAW_OFFSET = 90.0F;
    private static final float MAX_VISUAL_PITCH = 35.0F;
    private static final float MAX_VISUAL_SIDE_TILT = 18.0F;

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
        super.applyRotations(animatable, poseStack, ageInTicks, interpolatedYaw + MODEL_YAW_OFFSET, partialTick, nativeScale);
        float interpolatedPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.clamp(interpolatedPitch, -MAX_VISUAL_PITCH, MAX_VISUAL_PITCH)));
        float sideTilt = Mth.clamp(animatable.getSideTilt(), -MAX_VISUAL_SIDE_TILT, MAX_VISUAL_SIDE_TILT);
        poseStack.mulPose(Axis.XP.rotationDegrees(sideTilt));
    }
}
