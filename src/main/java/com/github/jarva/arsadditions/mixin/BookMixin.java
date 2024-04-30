package com.github.jarva.arsadditions.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.forge.xplat.ForgeXplatImpl;

@Mixin(Book.class)
public class BookMixin {
    @Shadow @Final public ResourceLocation id;

    @Inject(method = "reloadContents(Z)V", at = @At(value = "TAIL"), remap = false)
    private void fireBookReloadEvent(boolean singleBook, CallbackInfo ci) {
        ForgeXplatImpl.INSTANCE.fireBookReload(this.id);
    }
}
