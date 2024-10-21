package com.github.jarva.arsadditions.mixin.patchouli;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.page.PageRelations;

import java.util.List;

@Mixin(PageRelations.class)
public interface PageRelationsAccessor {
    @Accessor("entryObjs")
    List<BookEntry> getEntries();
}
