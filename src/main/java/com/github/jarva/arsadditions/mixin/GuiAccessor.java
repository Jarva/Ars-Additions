package com.github.jarva.arsadditions.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccessor {
    @Accessor
    void setOverlayMessageString(Component message);

    @Accessor
    void setOverlayMessageTime(int time);
}
