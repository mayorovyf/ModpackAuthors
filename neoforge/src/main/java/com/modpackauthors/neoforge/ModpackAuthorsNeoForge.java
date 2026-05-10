package com.modpackauthors.neoforge;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.client.menu.MainMenuButtonInjector;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ModpackAuthors.MOD_ID)
public final class ModpackAuthorsNeoForge {
    public ModpackAuthorsNeoForge(IEventBus modEventBus) {
        ModpackAuthors.init(new NeoForgePlatformServices());

        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onRegisterClientReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onScreenInit);
    }

    private void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new AuthorCatalogReloadListener());
    }

    private void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen titleScreen) {
            MainMenuButtonInjector.inject(titleScreen, event.getListenersList(), event::addListener);
        }
    }
}
