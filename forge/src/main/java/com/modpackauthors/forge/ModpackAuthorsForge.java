package com.modpackauthors.forge;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.client.menu.MainMenuButtonInjector;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.GuiScreenEvent;
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
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onScreenInit);
        event.enqueueWork(() -> {
            IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager instanceof IReloadableResourceManager) {
                ((IReloadableResourceManager) resourceManager).registerReloadListener(new AuthorCatalogReloadListener());
            }
        });
    }

    private void onScreenInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof MainMenuScreen) {
            MainMenuScreen titleScreen = (MainMenuScreen) event.getGui();
            MainMenuButtonInjector.inject(titleScreen, event.getWidgetList(), event::addWidget);
        }
    }
}
