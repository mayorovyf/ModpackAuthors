package com.modpackauthors.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public record LocalizedText(String fallback, Map<String, String> translations) {
    private static final String DEFAULT_KEY = "default";

    public LocalizedText {
        fallback = fallback == null ? "" : fallback;
        translations = Map.copyOf(translations == null ? Map.of() : translations);
    }

    public static LocalizedText of(String value) {
        return new LocalizedText(value, Map.of());
    }

    public static LocalizedText of(Map<String, String> translations) {
        Map<String, String> normalized = new LinkedHashMap<>();
        translations.forEach((key, value) -> {
            if (key != null && value != null && !value.isBlank()) {
                normalized.put(normalizeLanguage(key), value);
            }
        });

        String fallback = firstNonBlank(
                normalized.get(DEFAULT_KEY),
                normalized.get(LanguageManager.DEFAULT_LANGUAGE_CODE),
                normalized.values().stream().findFirst().orElse("")
        );
        return new LocalizedText(fallback, normalized);
    }

    public String resolve() {
        String selectedLanguage = selectedLanguage();
        return firstNonBlank(
                this.translations.get(selectedLanguage),
                this.translations.get(DEFAULT_KEY),
                this.translations.get(LanguageManager.DEFAULT_LANGUAGE_CODE),
                this.fallback,
                this.translations.values().stream().findFirst().orElse("")
        );
    }

    public boolean isBlank() {
        return resolve().isBlank();
    }

    private static String selectedLanguage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getLanguageManager() == null) {
            return LanguageManager.DEFAULT_LANGUAGE_CODE;
        }
        return normalizeLanguage(minecraft.options.languageCode);
    }

    private static String normalizeLanguage(String language) {
        return language == null ? "" : language.toLowerCase(Locale.ROOT);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
