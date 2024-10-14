package com.github.jarva.arsadditions.mixin.patchouli;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.patchouli.client.book.page.PageText;

@Mixin(PageText.class)
public interface PageTextAccessor {
    @Accessor("title")
    String getTitle();

    @Accessor("title")
    void setTitle(String title);
}
