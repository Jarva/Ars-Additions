package com.github.jarva.arsadditions.setup;

import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.ars_creo.contraption.SourceJarBehavior;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;

public class CreateSetup {
    public static void setup() {
        MovementBehaviour.REGISTRY.register(AddonBlockRegistry.ENDER_SOURCE_JAR.get(), new SourceJarBehavior());
    }
}