package com.modpackauthors.client;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.client.menu.MainMenuButtonInjector;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;

@Mod.EventBusSubscriber(modid = ModpackAuthors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModpackAuthorsClient {
    private ModpackAuthorsClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, MainMenuButtonInjector::onScreenInit);
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new AuthorCatalogReloadListener());
    }
}
