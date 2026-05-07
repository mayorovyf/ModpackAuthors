package com.modpackauthors.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class AuthorsClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue SHOW_MAIN_MENU_BUTTON;
    private static final ForgeConfigSpec.EnumValue<ButtonAnchor> BUTTON_ANCHOR;
    private static final ForgeConfigSpec.IntValue OFFSET_X;
    private static final ForgeConfigSpec.IntValue OFFSET_Y;
    private static final ForgeConfigSpec.IntValue BUTTON_WIDTH;
    private static final ForgeConfigSpec.IntValue BUTTON_HEIGHT;
    private static final ForgeConfigSpec.EnumValue<ScreenStyle> SCREEN_STYLE;

    static {
        BUILDER.push("mainMenuButton");
        SHOW_MAIN_MENU_BUTTON = BUILDER
                .comment("Whether to show the Authors button on the Minecraft title screen.")
                .define("showMainMenuButton", true);
        BUTTON_ANCHOR = BUILDER
                .comment("Where the Authors button is placed on the title screen.")
                .defineEnum("buttonAnchor", ButtonAnchor.BELOW_MULTIPLAYER);
        OFFSET_X = BUILDER
                .comment("Horizontal offset from the selected anchor.")
                .defineInRange("offsetX", 0, -10000, 10000);
        OFFSET_Y = BUILDER
                .comment("Vertical offset from the selected anchor.")
                .defineInRange("offsetY", 0, -10000, 10000);
        BUTTON_WIDTH = BUILDER
                .comment("Authors button width in GUI pixels.")
                .defineInRange("buttonWidth", 200, 40, 300);
        BUTTON_HEIGHT = BUILDER
                .comment("Authors button height in GUI pixels.")
                .defineInRange("buttonHeight", 20, 16, 60);
        BUILDER.pop();

        BUILDER.push("screen");
        SCREEN_STYLE = BUILDER
                .comment("Visual style for the authors screen.")
                .defineEnum("screenStyle", ScreenStyle.DEFAULT);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private AuthorsClientConfig() {
    }

    public static boolean showMainMenuButton() {
        return SHOW_MAIN_MENU_BUTTON.get();
    }

    public static ButtonAnchor buttonAnchor() {
        return BUTTON_ANCHOR.get();
    }

    public static int offsetX() {
        return OFFSET_X.get();
    }

    public static int offsetY() {
        return OFFSET_Y.get();
    }

    public static int buttonWidth() {
        return BUTTON_WIDTH.get();
    }

    public static int buttonHeight() {
        return BUTTON_HEIGHT.get();
    }

    public static ScreenStyle screenStyle() {
        return SCREEN_STYLE.get();
    }

    public enum ButtonAnchor {
        BELOW_MULTIPLAYER,
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
