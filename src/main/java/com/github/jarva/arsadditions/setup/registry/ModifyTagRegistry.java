package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.util.codec.TagModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsAdditions.MODID)
public class ModifyTagRegistry {
    private static final Codec<Codec<? extends TagModifier>> DIRECT_CODEC = ResourceLocation.CODEC.xmap(
            id -> getTagModifierRegistry().getValue(id),
            codec -> getTagModifierRegistry().getKey(codec)
    );
    public static final Codec<TagModifier> CODEC = DIRECT_CODEC.dispatch(TagModifier::type, Function.identity());

    public static final ResourceKey<Registry<Codec<? extends TagModifier>>> TAG_MODIFIER_KEY = ResourceKey.createRegistryKey(ArsAdditions.prefix("tag_modifier"));
    public static DeferredRegister<Codec<? extends TagModifier>> TAG_MODIFIER_DEFERRED = DeferredRegister.create(TAG_MODIFIER_KEY, ArsAdditions.MODID);

    private static IForgeRegistry<Codec<? extends TagModifier>> tagModifierRegistry;
    public static IForgeRegistry<Codec<? extends TagModifier>> getTagModifierRegistry() {
        return tagModifierRegistry;
    }

    @SubscribeEvent
    public static void AddRegistryEvent(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Codec<? extends TagModifier>>()
                .setName(TAG_MODIFIER_KEY.location())
                .disableSaving()
                .allowModification(), (b) -> tagModifierRegistry = b);
    }

    private static final RegistryObject<Codec<RemoveGuaranteedHandDrops>> REMOVE_GUARANTEED_HAND_DROPS = TAG_MODIFIER_DEFERRED.register("remove_guaranteed_hand_drops", () -> RemoveGuaranteedHandDrops.CODEC);
    private static final RegistryObject<Codec<RemoveTag>> REMOVE_TAG = TAG_MODIFIER_DEFERRED.register("remove_tag", () -> RemoveTag.CODEC);
    private static final RegistryObject<Codec<SetTag>> SET_TAG = TAG_MODIFIER_DEFERRED.register("set_tag", () -> SetTag.CODEC);
    private static final RegistryObject<Codec<AppendTag>> APPEND_TAG = TAG_MODIFIER_DEFERRED.register("append_tag", () -> AppendTag.CODEC);

    public record RemoveGuaranteedHandDrops() implements TagModifier {
        public static Codec<RemoveGuaranteedHandDrops> CODEC = Codec.unit(RemoveGuaranteedHandDrops::new);

        @Override
        public Codec<RemoveGuaranteedHandDrops> type() {
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
        public static Codec<RemoveTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("strip_tags").forGetter(RemoveTag::strip)
        ).apply(instance, RemoveTag::new));

        @Override
        public Codec<RemoveTag> type() {
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
        public static final Codec<SetTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, TAG_CODEC).fieldOf("set").forGetter(SetTag::set)
        ).apply(instance, SetTag::new));

        @Override
        public Codec<SetTag> type() {
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
        public static final Codec<AppendTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, TAG_CODEC).fieldOf("append").forGetter(AppendTag::append)
        ).apply(instance, AppendTag::new));

        @Override
        public Codec<AppendTag> type() {
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
