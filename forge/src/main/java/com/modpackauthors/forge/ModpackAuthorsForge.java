package com.modpackauthors.forge;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.client.menu.MainMenuButtonInjector;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModpackAuthors.MOD_ID)
public final class ModpackAuthorsForge {
    public ModpackAuthorsForge() {
        ModpackAuthors.init(new ForgePlatformServices());

        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        context.getModEventBus().addListener(this::onClientSetup);
        context.getModEventBus().addListener(this::onRegisterClientReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onScreenInit);
    }

    private void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new AuthorCatalogReloadListener());
    }

    private void onScreenInit(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof TitleScreen titleScreen) {
            MainMenuButtonInjector.inject(titleScreen, event.getListenersList(), event::addListener);
        }
    }
}
