package com.github.jarva.arsadditions.mixin.patchouli;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.patchouli.client.book.BookPage;

@Mixin(BookPage.class)
public interface BookPageAccessor {
    @Accessor("pageNum")
    void setPageNum(int i);
}
