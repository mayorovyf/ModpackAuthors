package com.modpackauthors.config;

import com.modpackauthors.ModpackAuthors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;

public final class AuthorsClientConfig {
    private static final String FILE_NAME = ModpackAuthors.MOD_ID + "-client.toml";

    private static boolean showMainMenuButton = true;
    private static ButtonAnchor buttonAnchor = ButtonAnchor.BELOW_MULTIPLAYER;
    private static int offsetX = 0;
    private static int offsetY = 0;
    private static int buttonWidth = 200;
    private static int buttonHeight = 20;
    private static ScreenStyle screenStyle = ScreenStyle.DEFAULT;

    private AuthorsClientConfig() {
    }

    public static synchronized void load(Path configDir) {
        Path configPath = configDir.resolve(FILE_NAME);
        resetDefaults();

        try {
            Files.createDirectories(configPath.getParent());
            if (Files.notExists(configPath)) {
                writeDefaultConfig(configPath);
            }
            readConfig(configPath);
        } catch (IOException exception) {
            ModpackAuthors.LOGGER.warn("Failed to load client config {}; using defaults", configPath, exception);
        }
    }

    private static void resetDefaults() {
        showMainMenuButton = true;
        buttonAnchor = ButtonAnchor.BELOW_MULTIPLAYER;
        offsetX = 0;
        offsetY = 0;
        buttonWidth = 200;
        buttonHeight = 20;
        screenStyle = ScreenStyle.DEFAULT;
    }

    private static void writeDefaultConfig(Path configPath) throws IOException {
        List<String> lines = List.of(
                "[mainMenuButton]",
                "showMainMenuButton = true",
                "buttonAnchor = \"BELOW_MULTIPLAYER\"",
                "offsetX = 0",
                "offsetY = 0",
                "buttonWidth = 200",
                "buttonHeight = 20",
                "",
                "[screen]",
                "screenStyle = \"DEFAULT\"",
                ""
        );
        Files.write(configPath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }

    private static void readConfig(Path configPath) throws IOException {
        String section = "";
        for (String rawLine : Files.readAllLines(configPath, StandardCharsets.UTF_8)) {
            String line = stripComment(rawLine).trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1).trim();
                continue;
            }

            int separator = line.indexOf('=');
            if (separator < 0) {
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = unquote(line.substring(separator + 1).trim());
            applyValue(configPath, section, key, value);
        }
    }

    private static String stripComment(String line) {
        int commentIndex = line.indexOf('#');
        return commentIndex < 0 ? line : line.substring(0, commentIndex);
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static void applyValue(Path configPath, String section, String key, String value) {
        try {
            if ("mainMenuButton".equals(section)) {
                applyMainMenuValue(key, value);
            } else if ("screen".equals(section) && "screenStyle".equals(key)) {
                screenStyle = parseEnum(ScreenStyle.class, value, screenStyle);
            }
        } catch (RuntimeException exception) {
            ModpackAuthors.LOGGER.warn("Ignoring invalid config value {}.{}={} in {}", section, key, value, configPath);
        }
    }

    private static void applyMainMenuValue(String key, String value) {
        switch (key) {
            case "showMainMenuButton" -> showMainMenuButton = Boolean.parseBoolean(value);
            case "buttonAnchor" -> buttonAnchor = parseEnum(ButtonAnchor.class, value, buttonAnchor);
            case "offsetX" -> offsetX = parseInt(value, offsetX, -10000, 10000);
            case "offsetY" -> offsetY = parseInt(value, offsetY, -10000, 10000);
            case "buttonWidth" -> buttonWidth = parseInt(value, buttonWidth, 40, 300);
            case "buttonHeight" -> buttonHeight = parseInt(value, buttonHeight, 16, 60);
            default -> {
            }
        }
    }

    private static int parseInt(String value, int fallback, int min, int max) {
        int parsed = Integer.parseInt(value);
        return Math.max(min, Math.min(max, parsed));
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> type, String value, T fallback) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (T constant : type.getEnumConstants()) {
            if (constant.name().equals(normalized)) {
                return constant;
            }
        }
        return fallback;
    }

    public static boolean showMainMenuButton() {
        return showMainMenuButton;
    }

    public static ButtonAnchor buttonAnchor() {
        return buttonAnchor;
    }

    public static int offsetX() {
        return offsetX;
    }

    public static int offsetY() {
        return offsetY;
    }

    public static int buttonWidth() {
        return buttonWidth;
    }

    public static int buttonHeight() {
        return buttonHeight;
    }

    public static ScreenStyle screenStyle() {
        return screenStyle;
    }

    public enum ButtonAnchor {
        BELOW_MULTIPLAYER,
        FANCYMENU_CENTER,
        CENTER,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        NEAR_OPTIONS,
        NEAR_MODS
    }

    public enum ScreenStyle {
        DEFAULT
    }
}
