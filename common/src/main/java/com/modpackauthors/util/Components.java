package com.modpackauthors.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public final class Components {
    private Components() {
    }

    public static Component empty() {
        return TextComponent.EMPTY;
    }

    public static Component literal(String text) {
        return new TextComponent(text);
    }

    public static Component translatable(String key) {
        return new TranslatableComponent(key);
    }
}
