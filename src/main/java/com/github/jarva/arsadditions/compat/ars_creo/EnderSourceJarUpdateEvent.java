package com.github.jarva.arsadditions.compat.ars_creo;

import net.neoforged.bus.api.Event;

import java.util.UUID;

public class EnderSourceJarUpdateEvent extends Event {
    public final int source;
    public final UUID uuid;

    public EnderSourceJarUpdateEvent(int source, UUID uuid) {
        this.source = source;
        this.uuid = uuid;
    }
}
