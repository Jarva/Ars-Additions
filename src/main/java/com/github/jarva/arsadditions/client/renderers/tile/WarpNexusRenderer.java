package com.github.jarva.arsadditions.client.renderers.tile;

import com.github.jarva.arsadditions.common.block.WarpNexus;
import com.github.jarva.arsadditions.common.block.tile.WarpNexusTile;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import software.bernie.ars_nouveau.geckolib3.core.builder.Animation;
import software.bernie.ars_nouveau.geckolib3.core.processor.IBone;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;

public class WarpNexusRenderer extends GeoBlockRenderer<WarpNexusTile> {
    public static AnimatedGeoModel<WarpNexusTile> model = new GenericModel<>("warp_nexus", "block");
    public WarpNexusRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void renderEarly(WarpNexusTile animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        try {
            if (animatable.getBlockState().getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
            if (animatable.getBlockState().getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

            renderItem(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(WarpNexusTile animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        try {
            if (animatable.getBlockState().getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
            if (animatable.getBlockState().getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

            super.render(animatable, partialTick, poseStack, bufferSource, packedLight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderItem(WarpNexusTile animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        try {
            if (animatable.getBlockState().getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;
            if (animatable.getBlockState().getValue(WarpNexus.HALF) != DoubleBlockHalf.LOWER) return;

            ItemStack stack = animatable.getStack();
            if (stack.isEmpty()) {
                Animation queued = animatable.controller.getCurrentAnimation();
                if (queued == null) return;
                if (!queued.animationName.equals("spin")) return;

                BlockPos pos = animatable.getBlockPos();

                ParticleColor nextColor = ParticleColor.defaultParticleColor().nextColor(animatable.getLevel().random);
                animatable.getLevel().addParticle(GlowParticleData.createData(nextColor),
                        pos.getX() + 0.5,
                        pos.getY() + 1,
                        pos.getZ() + 0.5,
                        0,
                        ParticleUtil.inRange(0.0, 0.01f),
                        0);
            } else {
                IBone bone = model.getBone("magic_cube");
                poseStack.pushPose();
                poseStack.translate(0, 1.0, 0);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Vector3f.YP.rotation(bone.getRotationY()));
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, poseStack, bufferSource, (int) animatable.getBlockPos().asLong());
                poseStack.popPose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GenericItemBlockRenderer getISTER() {
        return new GenericItemBlockRenderer(model);
    }

    @Override
    public RenderType getRenderType(WarpNexusTile animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntity blockEntity) {
        return false;
    }
}
