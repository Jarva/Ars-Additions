package com.github.jarva.arsadditions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;

import java.util.List;

@Mixin(BookEntry.class)
public interface BookEntryAccessor {
    @Accessor("realPages")
    List<BookPage> getRealPages();

    @Mutable
    @Accessor("realPages")
    void setRealPages(List<BookPage> pages);
}
