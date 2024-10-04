package com.github.jarva.arsadditions.common.util;

import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Locale;

public class LangUtil {
    public static Component storageLectern() {
        return Component.translatable("block.ars_nouveau.storage_lectern");
    }
    public static Component container() {
        return Component.translatable("tooltip.ars_additions.handy_haversack.container");
    }
    public static Component toTitleCase(String string) {
        String prepared = string
            .replace("_", " ")
            .toLowerCase(Locale.ENGLISH);
        return Component.literal(WordUtils.capitalizeFully(prepared));
    }
}
