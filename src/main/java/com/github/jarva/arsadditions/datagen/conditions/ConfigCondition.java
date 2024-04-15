package com.github.jarva.arsadditions.datagen.conditions;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.config.CommonConfig;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition implements ICondition {
    private static final ResourceLocation NAME = ArsAdditions.prefix("config");
    private final String configPath;

    public ConfigCondition(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public String toString() {
        return "config(\"" + configPath + "\")";
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext iContext) {
        return (boolean) CommonConfig.COMMON.config.get(this.configPath).get();
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject jsonObject, ConfigCondition iCondition) {
            jsonObject.addProperty("config", iCondition.configPath);
        }

        @Override
        public ConfigCondition read(JsonObject jsonObject) {
            return new ConfigCondition(GsonHelper.getAsString(jsonObject, "config"));
        }

        @Override
        public ResourceLocation getID() {
            return ConfigCondition.NAME;
        }
    }
}
