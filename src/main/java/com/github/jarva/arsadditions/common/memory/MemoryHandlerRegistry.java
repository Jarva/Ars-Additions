package com.github.jarva.arsadditions.common.memory;

import com.github.jarva.arsadditions.common.memory.handlers.ItemDetectorMemoryHandler;
import com.github.jarva.arsadditions.common.memory.handlers.RuneMemoryHandler;
import com.github.jarva.arsadditions.common.memory.handlers.SpellSensorMemoryHandler;
import com.github.jarva.arsadditions.common.memory.handlers.StarbuncleMemoryHandler;
import com.github.jarva.arsadditions.common.memory.handlers.TurretMemoryHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for memory handlers.
 * Handlers can be registered by any mod to add Memory Crystal support for their blocks/entities.
 */
public class MemoryHandlerRegistry {

    private static final Map<ResourceLocation, MemoryHandler> HANDLERS = new HashMap<>();

    public static void register(MemoryHandler handler) {
        HANDLERS.put(handler.getId(), handler);
    }

    public static MemoryHandler get(ResourceLocation id) {
        return HANDLERS.get(id);
    }

    public static Collection<MemoryHandler> getAllHandlers() {
        return HANDLERS.values();
    }

    static {
        register(new TurretMemoryHandler());
        register(new StarbuncleMemoryHandler());
        register(new RuneMemoryHandler());
        register(new SpellSensorMemoryHandler());
        register(new ItemDetectorMemoryHandler());
    }

    private MemoryHandlerRegistry() {}
}
