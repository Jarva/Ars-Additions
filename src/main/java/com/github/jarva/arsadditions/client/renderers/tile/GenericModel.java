package com.github.jarva.arsadditions.client.renderers.tile;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.resources.ResourceLocation;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;

public class GenericModel<T extends IAnimatable> extends com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel<T> {
    public GenericModel(String name) {
        super(name);
        this.modelLocation = new ResourceLocation(ArsAdditions.MODID, "geo/" + name + ".geo.json");
        this.textLoc = new ResourceLocation(ArsAdditions.MODID, "textures/" + textPathRoot + "/" + name + ".png");
        this.animationLoc = new ResourceLocation(ArsAdditions.MODID, "animations/" + name + "_animations.json");
        this.name = name;
    }

    public GenericModel(String name, String textPath) {
        this(name);
        this.textPathRoot = textPath;
        this.textLoc = new ResourceLocation(ArsAdditions.MODID, "textures/" + textPathRoot + "/" + name + ".png");
    }
}
