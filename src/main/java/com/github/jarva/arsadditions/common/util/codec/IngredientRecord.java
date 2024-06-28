package com.github.jarva.arsadditions.common.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public record IngredientRecord(Optional<TagKey<Item>> tag, Optional<Item> item) {
    public static final Codec<IngredientRecord> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(IngredientRecord::tag),
            ForgeRegistries.ITEMS.getCodec().optionalFieldOf("item").forGetter(IngredientRecord::item)
    ).apply(instance, IngredientRecord::new));

    public Ingredient getIngredient() {
        return tag.map(Ingredient::of).or(() -> item.map(Ingredient::of)).orElse(null);
    }

    public static IngredientRecord tag(TagKey<Item> tagKey) {
        return new IngredientRecord(Optional.of(tagKey), Optional.empty());
    }

    public static IngredientRecord item(Item item) {
        return new IngredientRecord(Optional.empty(), Optional.of(item));
    }
}
