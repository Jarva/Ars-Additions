package com.github.jarva.arsadditions.datagen.patchouli;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.IPatchouliPage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class ApparatusPageProvider implements IPatchouliPage {
    public JsonObject object = new JsonObject();

    @Override
    public JsonObject build() {
        object.addProperty("type", getType().toString());
        return object;
    }

    public ApparatusPageProvider(String recipe) {
        this.object.addProperty("recipe", recipe);
    }

    public ApparatusPageProvider(ItemLike itemLike) {
        this(itemLike, "apparatus/" );
    }

    public ApparatusPageProvider(ItemLike itemLike, String prefix) {
        this(getRegistryName(itemLike.asItem()).withPrefix(prefix).toString());
    }

    @Override
    public ResourceLocation getType() {
        return ArsNouveau.prefix( "apparatus_recipe");
    }
}

