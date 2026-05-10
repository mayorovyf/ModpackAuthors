package com.modpackauthors.client.menu;

import com.modpackauthors.client.screen.AuthorsScreen;
import com.modpackauthors.config.AuthorsClientConfig;
import com.modpackauthors.util.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public final class MainMenuButtonInjector {
    private static final int VANILLA_BUTTON_WIDTH = 200;
    private static final int VANILLA_BUTTON_SPACING = 24;
    private static final int TITLE_FIRST_BUTTON_Y_OFFSET = 48;

    private MainMenuButtonInjector() {
    }

    public static void inject(TitleScreen titleScreen, Collection<? extends GuiEventListener> listeners, Consumer<Button> addButton) {
        if (!AuthorsClientConfig.showMainMenuButton()) {
            return;
        }

        ButtonPlacement placement = computePlacement(listeners, titleScreen.width, titleScreen.height);

        if (AuthorsClientConfig.buttonAnchor() == AuthorsClientConfig.ButtonAnchor.BELOW_MULTIPLAYER) {
            shiftWidgetsAtOrBelow(listeners, placement.y(), placement.height() + 4);
        }

        Button button = new Button(placement.x(), placement.y(), placement.width(), placement.height(),
                Components.translatable("screen.modpack_authors.menu_button"),
                pressed -> Minecraft.getInstance().setScreen(new AuthorsScreen(titleScreen)));

        addButton.accept(button);
    }

    private static ButtonPlacement computePlacement(Collection<? extends GuiEventListener> listeners, int screenWidth, int screenHeight) {
        if (AuthorsClientConfig.buttonAnchor() == AuthorsClientConfig.ButtonAnchor.BELOW_MULTIPLAYER) {
            ButtonPlacement placement = belowMultiplayerPlacement(listeners, screenWidth, screenHeight);
            int x = Mth.clamp(placement.x() + AuthorsClientConfig.offsetX(), 0, Math.max(0, screenWidth - placement.width()));
            int y = Mth.clamp(placement.y() + AuthorsClientConfig.offsetY(), 0, Math.max(0, screenHeight - placement.height()));
            return new ButtonPlacement(x, y, placement.width(), placement.height());
        }

        int buttonWidth = AuthorsClientConfig.buttonWidth();
        int buttonHeight = AuthorsClientConfig.buttonHeight();
        int x = computeX(screenWidth, buttonWidth);
        int y = computeY(screenHeight, buttonHeight);
        return new ButtonPlacement(x, y, buttonWidth, buttonHeight);
    }

    private static ButtonPlacement belowMultiplayerPlacement(Collection<? extends GuiEventListener> listeners, int screenWidth, int screenHeight) {
        List<AbstractWidget> fullWidthButtons = new ArrayList<AbstractWidget>();
        for (GuiEventListener listener : listeners) {
            if (listener instanceof AbstractWidget) {
                AbstractWidget widget = (AbstractWidget) listener;
                if (widget.getWidth() >= VANILLA_BUTTON_WIDTH) {
                    fullWidthButtons.add(widget);
                }
            }
        }
        fullWidthButtons.sort(Comparator.comparingInt(widget -> widget.y));

        if (fullWidthButtons.size() >= 2) {
            AbstractWidget multiplayerButton = fullWidthButtons.get(1);
            return new ButtonPlacement(
                    multiplayerButton.x,
                    multiplayerButton.y + VANILLA_BUTTON_SPACING,
                    multiplayerButton.getWidth(),
                    multiplayerButton.getHeight()
            );
        }

        int buttonWidth = AuthorsClientConfig.buttonWidth();
        int buttonHeight = AuthorsClientConfig.buttonHeight();
        int x = (screenWidth - buttonWidth) / 2;
        int y = screenHeight / 4 + TITLE_FIRST_BUTTON_Y_OFFSET + VANILLA_BUTTON_SPACING * 2;
        return new ButtonPlacement(x, y, buttonWidth, buttonHeight);
    }

    private static int computeX(int screenWidth, int buttonWidth) {
        int x;
        switch (AuthorsClientConfig.buttonAnchor()) {
            case BELOW_MULTIPLAYER:
                x = screenWidth / 2 - VANILLA_BUTTON_WIDTH / 2;
                break;
            case CENTER:
                x = (screenWidth - buttonWidth) / 2;
                break;
            case BOTTOM_LEFT:
                x = 4;
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - buttonWidth;
                break;
            case NEAR_OPTIONS:
                x = screenWidth / 2 - 100;
                break;
            case NEAR_MODS:
            default:
                x = screenWidth / 2 + 2;
                break;
        }

        return Mth.clamp(x + AuthorsClientConfig.offsetX(), 0, Math.max(0, screenWidth - buttonWidth));
    }

    private static int computeY(int screenHeight, int buttonHeight) {
        int y;
        switch (AuthorsClientConfig.buttonAnchor()) {
            case BELOW_MULTIPLAYER:
                y = screenHeight / 4 + TITLE_FIRST_BUTTON_Y_OFFSET + VANILLA_BUTTON_SPACING * 2;
                break;
            case CENTER:
                y = screenHeight / 4 + 144;
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                y = screenHeight - buttonHeight;
                break;
            case NEAR_OPTIONS:
            case NEAR_MODS:
            default:
                y = screenHeight / 4 + 120;
                break;
        }

        return Mth.clamp(y + AuthorsClientConfig.offsetY(), 0, Math.max(0, screenHeight - buttonHeight));
    }

    private static void shiftWidgetsAtOrBelow(Collection<? extends GuiEventListener> listeners, int minY, int amount) {
        for (GuiEventListener listener : listeners) {
            if (listener instanceof AbstractWidget) {
                AbstractWidget widget = (AbstractWidget) listener;
                if (widget.y < minY) {
                    continue;
                }
                widget.y += amount;
            }
        }
    }

    private static final class ButtonPlacement {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private ButtonPlacement(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        private int x() {
            return this.x;
        }

        private int y() {
            return this.y;
        }

        private int width() {
            return this.width;
        }

        private int height() {
            return this.height;
        }
    }
}
