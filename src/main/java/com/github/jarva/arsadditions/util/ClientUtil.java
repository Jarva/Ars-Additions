package com.github.jarva.arsadditions.util;

import com.github.jarva.arsadditions.client.gui.WarpNexusScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ClientUtil {
    public static void openWarpScreen(ContainerLevelAccess access) {
        Minecraft.getInstance().setScreen(new WarpNexusScreen(access));
    }
}
