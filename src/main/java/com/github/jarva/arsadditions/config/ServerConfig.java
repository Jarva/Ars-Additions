package com.github.jarva.arsadditions.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {
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
    public final ForgeConfigSpec.IntValue reliquary_cost_player;
    public final ForgeConfigSpec.IntValue reliquary_cost_entity;
    public final ForgeConfigSpec.IntValue reliquary_cost_location;

    ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Ritual of Arcane Permanence").push("chunkloading");
        chunkloading_has_cost = builder.comment("Should the ritual cost source?").define("has_cost", true);
        chunkloading_repeat_cost = builder.comment("Should the ritual cost be continuous?").define("repeat_cost", true);
        chunkloading_cost = builder.comment("How much source should it cost to run the ritual?").defineInRange("cost", 10000, 1, 10000);
        chunkloading_cost_interval = builder.comment("How often should the ritual cost source? (in ticks, defaults to 1 in-game day)").defineInRange("interval", 24000, 1, Integer.MAX_VALUE);
        chunkloading_initial_radius = builder.comment("How far should the ritual chunk-load? (in chunks, 0 = 1x1, 1 = 3x3, 2 = 5x5, 3 = 7x7, 4 = 9x9)").defineInRange("initial_radius", 0, 0, Integer.MAX_VALUE);
        chunkloading_radius_incremental = builder.comment("Should the radius be able to be increased with an item?").define("radius_incremental", false);
        chunkloading_radius_increment_item = builder.comment("What item is required to increase the chunk-loading radius?").define("radius_increment_item", "ars_nouveau:source_gem_block");
        chunkloading_radius_increment_max = builder.comment("What's the maximum amount of augmented increases the ritual should accept?").defineInRange("radius_increment_max", 1, 1, Integer.MAX_VALUE);
        chunkloading_require_online = builder.comment("Should the ritual require the player who started it to be online?").define("require_online", true);
        chunkloading_player_limit = builder.comment("How many rituals should players be able to run?").defineInRange("max_rituals", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        builder.comment("Reliquary").push("mark_and_recall");
        reliquary_cost_player = builder.comment("How much durability should targeting a player with Recall cost?").defineInRange("cost_player", 1000, 0, 1000);
        reliquary_cost_entity = builder.comment("How much durability should targeting an entity with Recall cost?").defineInRange("cost_entity", 250, 0, 1000);
        reliquary_cost_location = builder.comment("How much durability should targeting a location with Recall cost?").defineInRange("cost_location", 50, 0, 1000);
    }

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = pair.getRight();
        SERVER = pair.getLeft();
    }
}
