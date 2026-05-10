package com.modpackauthors.fabric;

import com.modpackauthors.ModpackAuthors;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class ModpackAuthorsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModpackAuthors.init(new FabricPlatformServices());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricAuthorCatalogReloadListener());
    }
}
