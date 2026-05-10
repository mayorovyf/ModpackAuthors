package com.modpackauthors.fabric;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.client.menu.MainMenuButtonInjector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.server.packs.PackType;

public final class ModpackAuthorsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModpackAuthors.init(new FabricPlatformServices());

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricAuthorCatalogReloadListener());
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                TitleScreen titleScreen = (TitleScreen) screen;
                MainMenuButtonInjector.inject(titleScreen, Screens.getButtons(screen), button -> Screens.getButtons(screen).add(button));
            }
        });
    }
}
