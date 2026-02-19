package com.github.jarva.arsadditions.client.renderers.entity;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.entity.MagicCarpetEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicCarpetModel extends GeoModel<MagicCarpetEntity> {
    private static final ResourceLocation MODEL = ArsAdditions.prefix("geo/magic_carpet.geo.json");
    private static final ResourceLocation TEXTURE = ArsAdditions.prefix("textures/entity/magic_carpet.png");
    private static final ResourceLocation ANIMATIONS = ArsAdditions.prefix("animations/magic_carpet_animations.json");

    @Override
    public ResourceLocation getModelResource(MagicCarpetEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MagicCarpetEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MagicCarpetEntity animatable) {
        return ANIMATIONS;
    }
}
