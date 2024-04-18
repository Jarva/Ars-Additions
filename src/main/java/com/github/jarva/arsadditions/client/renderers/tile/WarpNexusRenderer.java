package com.github.jarva.arsadditions.client.renderers.tile;

import com.github.jarva.arsadditions.block.tile.WarpNexusTile;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.renderer.tile.ArsGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class WarpNexusRenderer extends ArsGeoBlockRenderer<WarpNexusTile> {
    public static GeoModel<WarpNexusTile> model = new GenericModel<>("warp_nexus");
    public WarpNexusRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, model);
    }

    @Override
    public void renderFinal(PoseStack poseStack, WarpNexusTile animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getLevel().getBlockState(animatable.getBlockPos()).getBlock() != AddonBlockRegistry.WARP_NEXUS.get()) return;


    }
}
