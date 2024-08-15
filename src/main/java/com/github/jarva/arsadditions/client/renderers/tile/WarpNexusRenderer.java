package com.github.jarva.arsadditions.client.renderers.tile;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.block.tile.WarpNexusTile;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.ArsGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class WarpNexusRenderer extends ArsGeoBlockRenderer<WarpNexusTile> {
    public static GeoModel<WarpNexusTile> model = new GenericModel<>("warp_nexus");
    public WarpNexusRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void preRender(PoseStack poseStack, WarpNexusTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, WarpNexusTile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
    }

    @Override
    public void renderFinal(PoseStack poseStack, WarpNexusTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

        ItemStack stack = animatable.getStack();
        if (stack.isEmpty()) {
            AnimationProcessor.QueuedAnimation queued = animatable.controller.getCurrentAnimation();
            if (queued == null) return;
            if (!queued.animation().name().equals("spin")) return;

            BlockPos pos = animatable.getBlockPos();

            ParticleColor nextColor = animatable.getColor().transition((int) (animatable.getLevel().getGameTime() * 10));
            animatable.getLevel().addParticle(GlowParticleData.createData(nextColor),
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    0,
                    ParticleUtil.inRange(0.0, 0.01f),
                    0);
        } else {
            model.getBone("magic_cube").ifPresent(bone -> {
                poseStack.pushPose();
                poseStack.translate(0.5, 1.0, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), animatable.getLevel(), (int) animatable.getBlockPos().asLong());
                poseStack.popPose();
            });
        }
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }
}
