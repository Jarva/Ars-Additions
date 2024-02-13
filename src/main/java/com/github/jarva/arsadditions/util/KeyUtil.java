package com.github.jarva.arsadditions.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class KeyUtil {
    public static boolean isHoldingShift() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue());
    }
}
