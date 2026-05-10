package com.modpackauthors.fabric;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ModpackAuthorsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModpackAuthors.init(new FabricPlatformServices());

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager instanceof ReloadableResourceManager) {
            ((ReloadableResourceManager) resourceManager).registerReloadListener(new AuthorCatalogReloadListener());
        }
    }
}
