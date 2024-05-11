package com.github.jarva.arsadditions.common.loot.functions;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonLootItemFunctionsRegistry;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ExplorationScrollFunction extends LootItemConditionalFunction {
    public static final TagKey<Structure> DEFAULT_DESTINATION = TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix("on_explorer_warp_scroll"));
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;
    private final TagKey<Structure> tag;
    private final ResourceKey<Structure> resource;
    private final int searchRadius;
    private final boolean skipKnownStructures;

    protected ExplorationScrollFunction(LootItemCondition[] predicates, TagKey<Structure> tagKey, ResourceKey<Structure> resourceKey, int searchRadius, boolean skipKnownStructures) {
        super(predicates);
        this.tag = tagKey;
        this.resource = resourceKey;
        this.searchRadius = searchRadius;
        this.skipKnownStructures = skipKnownStructures;
    }

    @Override
    public LootItemFunctionType getType() {
        return AddonLootItemFunctionsRegistry.EXPLORATION_SCROLL_TYPE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!stack.is(AddonItemRegistry.EXPLORATION_WARP_SCROLL.get())) return stack;

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);

        saveData(stack, origin);

        if (origin == null) return stack;

        ServerLevel level = context.getLevel();
        HolderSet<Structure> holderSet = this.resource != null ? LocateUtil.holderFromResource(level, this.resource) : LocateUtil.holderFromTag(level, this.tag);

        LocateUtil.locateWithState(stack, level, holderSet, BlockPos.containing(origin), searchRadius, skipKnownStructures);
        return stack;
    }

    private void saveData(ItemStack stack, Vec3 origin) {
        CompoundTag tag = stack.getOrCreateTag();

        if (this.resource != null) {
            tag.putString("resource", this.resource.location().toString());
        } else {
            tag.putString("tag", this.tag.location().toString());
        }

        if (origin != null) {
            CompoundTag pos = new CompoundTag();
            pos.putDouble("x", origin.x());
            pos.putDouble("y", origin.y());
            pos.putDouble("z", origin.z());
            tag.put("origin", pos);
        }

        tag.putInt("search_radius", this.searchRadius);
        tag.putBoolean("skip_known", this.skipKnownStructures);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ExplorationScrollFunction> {
        @Override
        public void serialize(JsonObject json, ExplorationScrollFunction value, JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            if (value.resource != null) {
                json.addProperty("resource", value.resource.location().toString());
            }

            if (value.tag != DEFAULT_DESTINATION) {
                json.addProperty("tag", value.tag.location().toString());
            }

            if (value.searchRadius != DEFAULT_SEARCH_RADIUS) {
                json.addProperty("search_radius", value.searchRadius);
            }

            if (value.skipKnownStructures != DEFAULT_SKIP_EXISTING) {
                json.addProperty("skip_existing_chunks", value.skipKnownStructures);
            }
        }

        @Override
        public ExplorationScrollFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            TagKey<Structure> tagKey = readTag(object);
            ResourceKey<Structure> resourceKey = readResource(object);

            int i = GsonHelper.getAsInt(object, "search_radius", DEFAULT_SEARCH_RADIUS);
            boolean bl = GsonHelper.getAsBoolean(object, "skip_existing_chunks", DEFAULT_SKIP_EXISTING);

            return new ExplorationScrollFunction(conditions, tagKey, resourceKey, i, bl);
        }

        private static TagKey<Structure> readTag(JsonObject jsonObject) {
            if (jsonObject.has("tag")) {
                String string = GsonHelper.getAsString(jsonObject, "tag");
                return TagKey.create(Registries.STRUCTURE, new ResourceLocation(string));
            }
            return DEFAULT_DESTINATION;
        }

        private static ResourceKey<Structure> readResource(JsonObject jsonObject) {
            if (jsonObject.has("resource")) {
                String string = GsonHelper.getAsString(jsonObject, "resource");
                return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(string));
            }
            return null;
        }
    }
}
