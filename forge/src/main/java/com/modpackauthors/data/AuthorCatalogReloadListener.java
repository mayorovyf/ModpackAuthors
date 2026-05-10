package com.modpackauthors.data;

import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public class AuthorCatalogReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        AuthorCatalogLoader.setCachedCatalog(AuthorCatalogLoader.load(resourceManager));
    }
}
