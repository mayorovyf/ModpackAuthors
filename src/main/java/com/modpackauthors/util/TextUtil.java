package com.modpackauthors.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public final class TextUtil {
    private static final String ELLIPSIS = "...";

    private TextUtil() {
    }

    public static Component ellipsize(Font font, String text, int width) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        if (font.width(text) <= width) {
            return Component.literal(text);
        }

        int available = Math.max(0, width - font.width(ELLIPSIS));
        return Component.literal(font.plainSubstrByWidth(text, available) + ELLIPSIS);
    }
}
