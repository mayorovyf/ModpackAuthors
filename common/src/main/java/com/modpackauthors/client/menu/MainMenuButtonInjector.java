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
        List<AbstractWidget> fullWidthButtons = listeners.stream()
                .filter(AbstractWidget.class::isInstance)
                .map(AbstractWidget.class::cast)
                .filter(widget -> widget.getWidth() >= VANILLA_BUTTON_WIDTH)
                .sorted(Comparator.comparingInt(widget -> widget.y))
                .toList();

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
        int x = switch (AuthorsClientConfig.buttonAnchor()) {
            case BELOW_MULTIPLAYER -> screenWidth / 2 - VANILLA_BUTTON_WIDTH / 2;
            case CENTER -> (screenWidth - buttonWidth) / 2;
            case BOTTOM_LEFT -> 4;
            case BOTTOM_RIGHT -> screenWidth - buttonWidth;
            case NEAR_OPTIONS -> screenWidth / 2 - 100;
            case NEAR_MODS -> screenWidth / 2 + 2;
        };

        return Mth.clamp(x + AuthorsClientConfig.offsetX(), 0, Math.max(0, screenWidth - buttonWidth));
    }

    private static int computeY(int screenHeight, int buttonHeight) {
        int y = switch (AuthorsClientConfig.buttonAnchor()) {
            case BELOW_MULTIPLAYER -> screenHeight / 4 + TITLE_FIRST_BUTTON_Y_OFFSET + VANILLA_BUTTON_SPACING * 2;
            case CENTER -> screenHeight / 4 + 144;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - buttonHeight;
            case NEAR_OPTIONS, NEAR_MODS -> screenHeight / 4 + 120;
        };

        return Mth.clamp(y + AuthorsClientConfig.offsetY(), 0, Math.max(0, screenHeight - buttonHeight));
    }

    private static void shiftWidgetsAtOrBelow(Collection<? extends GuiEventListener> listeners, int minY, int amount) {
        for (GuiEventListener listener : listeners) {
            if (listener instanceof AbstractWidget widget && widget.y >= minY) {
                widget.y += amount;
            }
        }
    }

    private record ButtonPlacement(int x, int y, int width, int height) {
    }
}
