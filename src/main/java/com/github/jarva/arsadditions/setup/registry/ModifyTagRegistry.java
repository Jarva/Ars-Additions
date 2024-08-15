package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.util.codec.RegistryDispatcher;
import com.github.jarva.arsadditions.common.util.codec.TagModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

public class ModifyTagRegistry {
    public static final RegistryDispatcher<TagModifier> TAG_MODIFIER_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
            ArsAdditions.prefix("tag_modifier"),
            TagModifier::type,
            builder->{});

    private static final DeferredHolder<MapCodec<? extends TagModifier>, MapCodec<RemoveGuaranteedHandDrops>> REMOVE_GUARANTEED_HAND_DROPS = TAG_MODIFIER_DISPATCHER.defreg().register("remove_guaranteed_hand_drops", () -> RemoveGuaranteedHandDrops.CODEC);
    private static final DeferredHolder<MapCodec<? extends TagModifier>, MapCodec<RemoveTag>> REMOVE_TAG = TAG_MODIFIER_DISPATCHER.defreg().register("remove_tag", () -> RemoveTag.CODEC);
    private static final DeferredHolder<MapCodec<? extends TagModifier>, MapCodec<SetTag>> SET_TAG = TAG_MODIFIER_DISPATCHER.defreg().register("set_tag", () -> SetTag.CODEC);
    private static final DeferredHolder<MapCodec<? extends TagModifier>, MapCodec<AppendTag>> APPEND_TAG = TAG_MODIFIER_DISPATCHER.defreg().register("append_tag", () -> AppendTag.CODEC);

    public static void init() {

    }

    public record RemoveGuaranteedHandDrops() implements TagModifier {
        public static MapCodec<RemoveGuaranteedHandDrops> CODEC = MapCodec.unit(RemoveGuaranteedHandDrops::new);

        @Override
        public MapCodec<RemoveGuaranteedHandDrops> type() {
            return REMOVE_GUARANTEED_HAND_DROPS.get();
        }

        private void removeGuaranteedDrops(CompoundTag tag, String key) {
            if (!tag.contains(key)) return;

            ListTag list = tag.getList(key, Tag.TAG_FLOAT);
            for (int i = 0; i < list.size(); i++) {
                float chance = list.getFloat(i);
                if (chance == 2.0F) {
                    list.set(i, FloatTag.valueOf(-327.67F));
                }
            }
        }

        @Override
        public void modify(CompoundTag nbt) {
            removeGuaranteedDrops(nbt, "HandDropChances");
            removeGuaranteedDrops(nbt, "ArmorDropChances");
        }
    }

    public record RemoveTag(List<String> strip) implements TagModifier {
        public static MapCodec<RemoveTag> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("strip_tags").forGetter(RemoveTag::strip)
        ).apply(instance, RemoveTag::new));

        @Override
        public MapCodec<RemoveTag> type() {
            return REMOVE_TAG.get();
        }

        private void removeTag(CompoundTag nbt, ArrayDeque<String> compounds) {
            if (nbt.isEmpty()) return;
            if (compounds.isEmpty()) return;
            String first = compounds.pollFirst();
            if (compounds.isEmpty()) {
                nbt.remove(first);
                return;
            }
            removeTag(nbt.getCompound(first), compounds);
        }

        @Override
        public void modify(CompoundTag nbt) {
            for (String tag : strip()) {
                ArrayDeque<String> compounds = new ArrayDeque<>(List.of(tag.split("\\.")));
                removeTag(nbt, compounds);
            }
        }
    }

    public record SetTag(Map<String, Tag> set) implements TagModifier {
        private static final Codec<Tag> TAG_CODEC = Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(), tag -> new Dynamic<>(NbtOps.INSTANCE, tag));
        public static final MapCodec<SetTag> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, TAG_CODEC).fieldOf("set").forGetter(SetTag::set)
        ).apply(instance, SetTag::new));

        @Override
        public MapCodec<SetTag> type() {
            return SET_TAG.get();
        }

        private void setTag(CompoundTag nbt, ArrayDeque<String> compounds, Tag value) {
            if (compounds.isEmpty()) return;
            String first = compounds.pollFirst();
            if (compounds.isEmpty()) {
                nbt.put(first, value);
                return;
            }
            setTag(nbt.getCompound(first), compounds, value);
        }

        @Override
        public void modify(CompoundTag nbt) {
            for (Map.Entry<String, Tag> entry : set().entrySet()) {
                String tag = entry.getKey();
                Tag value = entry.getValue();
                ArrayDeque<String> compounds = new ArrayDeque<>(List.of(tag.split("\\.")));
                setTag(nbt, compounds, value);
            }
        }
    }

    public record AppendTag(Map<String, Tag> append) implements TagModifier {
        private static final Codec<Tag> TAG_CODEC = Codec.PASSTHROUGH.xmap(dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(), tag -> new Dynamic<>(NbtOps.INSTANCE, tag));
        public static final MapCodec<AppendTag> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, TAG_CODEC).fieldOf("append").forGetter(AppendTag::append)
        ).apply(instance, AppendTag::new));

        @Override
        public MapCodec<AppendTag> type() {
            return APPEND_TAG.get();
        }

        private void appendTag(CompoundTag nbt, ArrayDeque<String> compounds, Tag value) {
            if (compounds.isEmpty()) return;
            String first = compounds.pollFirst();
            if (compounds.isEmpty()) {
                ListTag list = nbt.getList(first, value.getId());
                list.add(value);
                nbt.put(first, list);
                return;
            }
            appendTag(nbt.getCompound(first), compounds, value);
        }

        @Override
        public void modify(CompoundTag nbt) {
            for (Map.Entry<String, Tag> entry : append().entrySet()) {
                String tag = entry.getKey();
                Tag value = entry.getValue();
                ArrayDeque<String> compounds = new ArrayDeque<>(List.of(tag.split("\\.")));
                appendTag(nbt, compounds, value);
            }
        }
    }
}
