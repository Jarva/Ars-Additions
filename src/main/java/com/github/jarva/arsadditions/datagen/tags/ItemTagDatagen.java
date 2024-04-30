package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagDatagen extends ItemTagsProvider {
    public static TagKey<Item> FORGOTTEN_KNOWLEDGE_GLYPHS = ItemTags.create(new ResourceLocation(ArsAdditions.MODID, "forgotten_knowledge"));
    public ItemTagDatagen(DataGenerator arg, BlockTagsProvider p_126531_, ExistingFileHelper helper) {
        super(arg, p_126531_, ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags() {
        this.tag(FORGOTTEN_KNOWLEDGE_GLYPHS);
    }
}
