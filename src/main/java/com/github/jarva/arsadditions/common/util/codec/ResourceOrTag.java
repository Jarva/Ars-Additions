package com.github.jarva.arsadditions.common.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record ResourceOrTag<T>(Optional<TagKey<T>> tag, Optional<ResourceKey<T>> key) {
    public static final Codec<ResourceOrTag<EntityType<?>>> ENTITY_TYPE_CODEC = createCodec(Registries.ENTITY_TYPE);
    public static final Codec<ResourceOrTag<Item>> ITEM_CODEC = createCodec(Registries.ITEM);
    public static final Codec<ResourceOrTag<Structure>> STRUCTURE_CODEC = createCodec(Registries.STRUCTURE);

    public static <T> Codec<ResourceOrTag<T>> createCodec(ResourceKey<Registry<T>> registry) {
        return RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(registry).optionalFieldOf("tag").forGetter(rec -> rec.tag),
            ResourceKey.codec(registry).optionalFieldOf("key").forGetter(rec -> rec.key)
        ).apply(instance, ResourceOrTag::new));
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, ResourceOrTag<T>> createStreamCodec(ResourceKey<Registry<T>> registry) {
        return StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.fromCodec(TagKey.codec(registry))), ResourceOrTag::tag,
                ByteBufCodecs.optional(ResourceKey.streamCodec(registry)), ResourceOrTag::key,
                ResourceOrTag::new
        );
    }

    public void apply(Consumer<TagKey<T>> tagConsumer, Consumer<ResourceKey<T>> keyConsumer) {
        this.tag.ifPresent(tagConsumer);
        this.key.ifPresent(keyConsumer);
    }

    public <R> Optional<R> map(Function<TagKey<T>, R> tagConsumer, Function<ResourceKey<T>, R> keyConsumer) {
        return this.tag.map(tagConsumer).or(() -> this.key.map(keyConsumer));
    }

    public static <T> ResourceOrTag<T> tag(TagKey<T> tag) {
        return new ResourceOrTag<>(Optional.of(tag), Optional.empty());
    }

    public static <T> ResourceOrTag<T> key(ResourceKey<T> key) {
        return new ResourceOrTag<>(Optional.empty(), Optional.of(key));
    }

    public static ResourceOrTag<Item> item(Item item) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(item));
        return ResourceOrTag.key(key);
    }

    public static ResourceOrTag<Block> block(Block block) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block));
        return ResourceOrTag.key(key);
    }
}
