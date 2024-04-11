package com.github.jarva.arsadditions.client.renderers.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.item.UnstableReliquary;
import com.github.jarva.arsadditions.util.MarkType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.RenderTypeHelper;

import java.util.Optional;

public class ReliquaryRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ResourceLocation MODEL_LOCATION = ArsAdditions.prefix("item/reliquary_base");
    public ReliquaryRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();

        poseStack.popPose();
        poseStack.pushPose();

        BakedModel mainModel = Minecraft.getInstance().getModelManager().getModel(MODEL_LOCATION);
        mainModel = mainModel.applyTransform(displayContext, poseStack, isLeftHand(displayContext));
        poseStack.translate(-.5, -.5, -.5); // Replicate ItemRenderer's translation

        boolean glint = stack.hasFoil();
        for (RenderType type : mainModel.getRenderTypes(stack, true)) {
            type = RenderTypeHelper.getEntityRenderType(type, true);
            VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, type, true, glint);
            renderer.renderModelLists(mainModel, stack, packedLight, packedOverlay, poseStack, consumer);
        }

        if (displayContext != ItemDisplayContext.GUI) return;

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        MarkType type = UnstableReliquary.getMarkType(stack);
        CompoundTag data = tag.getCompound("mark_data");

        if (type == MarkType.ENTITY) {
            String id = data.getString("entity_type");
            Optional<EntityType<?>> entityType = EntityType.byString(id);
            if (entityType.isPresent()) {
                Entity entity = entityType.get().create(Minecraft.getInstance().level);
                if (entity == null) return;

                float f = 0.53125F;
                float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());

                if (f1 > 1.0f) {
                    f /= (float) (f1 * 1.0);
                }

                Vec3 scale = new Vec3(f, f, f).multiply(new Vec3(1,1,1));
                Vec3 translate = new Vec3(0.5, 0.25, 0.5);
                poseStack.translate(translate.x, translate.y, translate.z);
                poseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);
                poseStack.mulPose(Axis.YP.rotationDegrees(15.0F));
                entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 180F, 0.0F, poseStack, buffer, packedLight);
            }
        }
    }

    private static boolean isLeftHand(ItemDisplayContext type)
    {
        return type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }
}
