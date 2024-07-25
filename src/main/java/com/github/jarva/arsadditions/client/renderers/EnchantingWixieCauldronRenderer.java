package com.github.jarva.arsadditions.client.renderers;

import com.github.jarva.arsadditions.common.block.tile.EnchantingWixieCauldronTile;
import com.github.jarva.arsadditions.mixin.WixieCauldronAccessor;
import com.hollingsworth.arsnouveau.client.renderer.tile.ArsGeoBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtil;

public class EnchantingWixieCauldronRenderer extends ArsGeoBlockRenderer<EnchantingWixieCauldronTile> {
    public static GeoModel<EnchantingWixieCauldronTile> model = new GenericModel<>("enchanting_apparatus");
    public EnchantingWixieCauldronRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void renderFinal(PoseStack poseStack, EnchantingWixieCauldronTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);

        WixieCauldronAccessor wixie = (WixieCauldronAccessor) animatable;
        ItemStack stack = wixie.getStackBeingCrafted();
        if (stack == null || stack.isEmpty()) return;

        model.getBone("frame_all").ifPresent(frame -> {
            poseStack.pushPose();
            RenderUtil.translateMatrixToBone(poseStack, frame);
            poseStack.translate(0.5, +0.5, 0.5);
            poseStack.scale(0.75f, 0.75f, 0.75f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, animatable.getLevel(), (int) animatable.getBlockPos().asLong());
            poseStack.popPose();
        });
    }
}
