package com.github.jarva.arsadditions.client.util;

import com.github.jarva.arsadditions.client.gui.WarpNexusScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ClientUtil {
    public static void openWarpScreen(ContainerLevelAccess access, ItemStackHandler itemStackHandler) {
        Minecraft.getInstance().setScreen(new WarpNexusScreen(access, itemStackHandler));
    }
}
