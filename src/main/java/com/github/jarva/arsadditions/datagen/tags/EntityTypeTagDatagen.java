package com.github.jarva.arsadditions.datagen.tags;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagDatagen extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public static TagKey<EntityType<?>> SOURCE_SPAWNER_DENYLIST = TagKey.create(Registries.ENTITY_TYPE, ArsAdditions.prefix("source_spawner_denylist"));
    public static TagKey<EntityType<?>> SOURCE_SPAWNER_NBT_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, ArsAdditions.prefix("source_spawner_nbt_blacklist"));
    public static TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "bosses"));
    public EntityTypeTagDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(arg, Registries.ENTITY_TYPE, future, item -> item.builtInRegistryHolder().key(), ArsAdditions.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(SOURCE_SPAWNER_DENYLIST).addTag(EntityTags.JAR_RELEASE_BLACKLIST).addTag(BOSSES);
        this.tag(SOURCE_SPAWNER_NBT_BLACKLIST);
    }
}
