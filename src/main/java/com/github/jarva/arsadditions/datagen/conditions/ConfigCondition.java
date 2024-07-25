package com.github.jarva.arsadditions.datagen.conditions;

import com.github.jarva.arsadditions.setup.config.CommonConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public record ConfigCondition(String configPath) implements ICondition {
    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(ConfigCondition::configPath)
    ).apply(instance, ConfigCondition::new));

    @Override
    public String toString() {
        return "config(\"" + configPath + "\")";
    }

    @Override
    public boolean test(IContext iContext) {
        return (boolean) CommonConfig.COMMON.config.get(this.configPath).get();
    }

    @Override
    public MapCodec<ConfigCondition> codec() {
        return CODEC;
    }
}
