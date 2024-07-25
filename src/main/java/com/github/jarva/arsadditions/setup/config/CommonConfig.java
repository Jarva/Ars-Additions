package com.github.jarva.arsadditions.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.function.Function;

public class CommonConfig {
    public final HashMap<String, ModConfigSpec.ConfigValue<?>> config = new HashMap<>();
    private final ModConfigSpec.BooleanValue chunkloading_recipe_enabled;
    private final ModConfigSpec.BooleanValue ruined_warp_portals_enabled;
    private final ModConfigSpec.BooleanValue nexus_tower_enabled;
    private final ModConfigSpec.BooleanValue arcane_library_enabled;

    CommonConfig(ModConfigSpec.Builder builder) {
        builder.comment("Ritual of Arcane Permanence").push("chunkloading");
        chunkloading_recipe_enabled = addConfig("ritual_enabled", (name) -> builder.comment("Should the default recipe for the ritual be enabled?").define(name, false));
        builder.pop();
        builder.comment("Structures").push("structures");
        ruined_warp_portals_enabled = addConfig("ruined_warp_portals_enabled", (name) -> builder.comment("Should ruined warp portals spawn in the world?").define(name, true));
        nexus_tower_enabled = addConfig("nexus_tower_enabled", (name) -> builder.comment("Should nexus towers spawn in the world?").define(name, true));
        arcane_library_enabled = addConfig("arcane_library_enabled", (name) -> builder.comment("Should arcane libraries spawn in the world?").define(name, false));
        builder.pop();
    }

    public <T extends ModConfigSpec.ConfigValue<?>> T addConfig(String name, Function<String, T> consumer) {
        T value = consumer.apply(name);
        config.put(name, value);
        return value;
    }

    public static final CommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }
}
