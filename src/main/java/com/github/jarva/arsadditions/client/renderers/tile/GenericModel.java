package com.github.jarva.arsadditions.client.renderers.tile;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;

public class GenericModel<T extends GeoAnimatable> extends com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel<T> {
    public GenericModel(String name) {
        super(name);
        this.modelLocation = ResourceLocation.fromNamespaceAndPath(ArsAdditions.MODID, "geo/" + name + ".geo.json");
        this.textLoc = ResourceLocation.fromNamespaceAndPath(ArsAdditions.MODID, "textures/" + textPathRoot + "/" + name + ".png");
        this.animationLoc = ResourceLocation.fromNamespaceAndPath(ArsAdditions.MODID, "animations/" + name + "_animations.json");
        this.name = name;
    }

    public GenericModel(String name, String textPath) {
        this(name);
        this.textPathRoot = textPath;
        this.textLoc = ResourceLocation.fromNamespaceAndPath(ArsAdditions.MODID, "textures/" + textPathRoot + "/" + name + ".png");
    }
}
