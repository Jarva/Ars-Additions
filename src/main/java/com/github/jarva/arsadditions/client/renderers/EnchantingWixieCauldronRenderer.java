package com.github.jarva.arsadditions.client.renderers;

import com.github.jarva.arsadditions.common.block.tile.EnchantingWixieCauldronTile;
import com.github.jarva.arsadditions.mixin.WixieCauldronAccessor;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import software.bernie.ars_nouveau.geckolib3.geo.render.built.GeoBone;
import software.bernie.ars_nouveau.geckolib3.model.AnimatedGeoModel;
import software.bernie.ars_nouveau.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.ars_nouveau.geckolib3.util.RenderUtils;

public class EnchantingWixieCauldronRenderer extends GeoBlockRenderer<EnchantingWixieCauldronTile> {
    public static AnimatedGeoModel<EnchantingWixieCauldronTile> model = new GenericModel<>("enchanting_apparatus");
    public EnchantingWixieCauldronRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void renderEarly(EnchantingWixieCauldronTile animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        try {
            if (animatable.getBlockState().getBlock() != AddonBlockRegistry.WIXIE_ENCHANTING.get()) return;

            renderItem(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderItem(EnchantingWixieCauldronTile animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        WixieCauldronAccessor wixie = (WixieCauldronAccessor) animatable;
        ItemStack stack = wixie.getStackBeingCrafted();
        if (stack == null || stack.isEmpty()) return;

        GeoBone bone = (GeoBone) model.getBone("frame_all");
        poseStack.pushPose();
        RenderUtils.translateMatrixToBone(poseStack, bone);
        poseStack.translate(0.5, +0.5, 0.5);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, (int) animatable.getBlockPos().asLong());
        poseStack.popPose();
    }
}
