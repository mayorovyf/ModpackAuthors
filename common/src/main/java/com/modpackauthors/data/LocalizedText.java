package com.modpackauthors.data;

import com.modpackauthors.util.JavaCompat;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class LocalizedText {
    private static final String DEFAULT_KEY = "default";
    private static final String DEFAULT_LANGUAGE_CODE = "en_us";

    private final String fallback;
    private final Map<String, String> translations;

    public LocalizedText(String fallback, Map<String, String> translations) {
        this.fallback = fallback == null ? "" : fallback;
        this.translations = JavaCompat.immutableMap(translations);
    }

    public String fallback() {
        return this.fallback;
    }

    public Map<String, String> translations() {
        return this.translations;
    }

    public static LocalizedText of(String value) {
        return new LocalizedText(value, Collections.<String, String>emptyMap());
    }

    public static LocalizedText of(Map<String, String> translations) {
        Map<String, String> normalized = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && !JavaCompat.isBlank(value)) {
                normalized.put(normalizeLanguage(key), value);
            }
        }

        String fallback = firstNonBlank(
                normalized.get(DEFAULT_KEY),
                normalized.get(DEFAULT_LANGUAGE_CODE),
                normalized.isEmpty() ? "" : normalized.values().iterator().next()
        );
        return new LocalizedText(fallback, normalized);
    }

    public String resolve() {
        String selectedLanguage = selectedLanguage();
        return firstNonBlank(
                this.translations.get(selectedLanguage),
                this.translations.get(DEFAULT_KEY),
                this.translations.get(DEFAULT_LANGUAGE_CODE),
                this.fallback,
                this.translations.isEmpty() ? "" : this.translations.values().iterator().next()
        );
    }

    public boolean isBlank() {
        return JavaCompat.isBlank(resolve());
    }

    private static String selectedLanguage() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getLanguageManager() == null) {
            return DEFAULT_LANGUAGE_CODE;
        }
        return normalizeLanguage(minecraft.options.languageCode);
    }

    private static String normalizeLanguage(String language) {
        return language == null ? "" : language.toLowerCase(Locale.ROOT);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!JavaCompat.isBlank(value)) {
                return value;
            }
        }
        return "";
    }
}
