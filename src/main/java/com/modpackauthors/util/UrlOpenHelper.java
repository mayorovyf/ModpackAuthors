package com.modpackauthors.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;

public final class UrlOpenHelper {
    private UrlOpenHelper() {
    }

    public static void confirmOpen(Screen returnScreen, String url) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getPlatform().openUri(url);
            }
            minecraft.setScreen(returnScreen);
        }, url, true));
    }
}
