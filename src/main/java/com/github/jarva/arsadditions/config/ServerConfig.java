package com.github.jarva.arsadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.function.Function;

public class ServerConfig {
    public final HashMap<String, ForgeConfigSpec.ConfigValue<?>> config = new HashMap<>();
    public final ForgeConfigSpec.IntValue chunkloading_cost;
    public final ForgeConfigSpec.BooleanValue chunkloading_has_cost;
    public final ForgeConfigSpec.BooleanValue chunkloading_repeat_cost;
    public final ForgeConfigSpec.IntValue chunkloading_cost_interval;
    public final ForgeConfigSpec.BooleanValue chunkloading_require_online;
    public final ForgeConfigSpec.IntValue chunkloading_initial_radius;
    public final ForgeConfigSpec.BooleanValue chunkloading_radius_incremental;
    public final ForgeConfigSpec.ConfigValue<String> chunkloading_radius_increment_item;
    public final ForgeConfigSpec.IntValue chunkloading_radius_increment_max;
    public final ForgeConfigSpec.IntValue chunkloading_player_limit;
    public final ForgeConfigSpec.BooleanValue chunkloading_log_loading;
    public final ForgeConfigSpec.IntValue reliquary_cost_player;
    public final ForgeConfigSpec.IntValue reliquary_cost_entity;
    public final ForgeConfigSpec.IntValue reliquary_cost_location;

    ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Ritual of Arcane Permanence").push("chunkloading");
        chunkloading_has_cost = addConfig( "has_cost", (name) -> builder.comment("Should the ritual cost source?").define(name, true));
        chunkloading_repeat_cost = addConfig("repeat_cost", (name) -> builder.comment("Should the ritual cost be continuous?").define(name, true));
        chunkloading_cost = addConfig("cost", (name) -> builder.comment("How much source should it cost to run the ritual?").defineInRange(name, 10000, 1, 10000));
        chunkloading_cost_interval = addConfig("interval", (name) -> builder.comment("How often should the ritual cost source? (in ticks, defaults to 1 in-game day)").defineInRange(name, 24000, 1, Integer.MAX_VALUE));
        chunkloading_initial_radius = addConfig("initial_radius", (name) -> builder.comment("How far should the ritual chunk-load? (in chunks, 0 = 1x1, 1 = 3x3, 2 = 5x5, 3 = 7x7, 4 = 9x9)").defineInRange(name, 0, 0, Integer.MAX_VALUE));
        chunkloading_radius_incremental = addConfig("radius_incremental", (name) -> builder.comment("Should the radius be able to be increased with an item?").define(name, false));
        chunkloading_radius_increment_item = addConfig("radius_increment_item", (name) -> builder.comment("What item is required to increase the chunk-loading radius?").define(name, "ars_nouveau:source_gem_block"));
        chunkloading_radius_increment_max = addConfig("radius_increment_max", (name) -> builder.comment("What's the maximum amount of augmented increases the ritual should accept?").defineInRange(name, 1, 1, Integer.MAX_VALUE));
        chunkloading_require_online = addConfig("require_online", (name) -> builder.comment("Should the ritual require the player who started it to be online?").define(name, true));
        chunkloading_player_limit = addConfig("max_rituals", (name) -> builder.comment("How many rituals should players be able to run?").defineInRange(name, Integer.MAX_VALUE, 1, Integer.MAX_VALUE));
        chunkloading_log_loading = addConfig("log_chunkloading", (name) -> builder.comment("Should the server log when a chunk is loaded/unloaded?").define(name, false));
        builder.pop();

        builder.comment("Reliquary").push("mark_and_recall");
        reliquary_cost_player = addConfig("cost_player", (name) -> builder.comment("How much durability should targeting a player with Recall cost?").defineInRange(name, 1000, 0, 1000));
        reliquary_cost_entity = addConfig("cost_entity", (name) -> builder.comment("How much durability should targeting an entity with Recall cost?").defineInRange(name, 250, 0, 1000));
        reliquary_cost_location = addConfig("cost_location", (name) -> builder.comment("How much durability should targeting a location with Recall cost?").defineInRange(name, 50, 0, 1000));
    }

    public <T extends ForgeConfigSpec.ConfigValue<?>> T addConfig(String name, Function<String, T> consumer) {
        T value = consumer.apply(name);
        config.put(name, value);
        return value;
    }

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = pair.getRight();
        SERVER = pair.getLeft();
    }
}
