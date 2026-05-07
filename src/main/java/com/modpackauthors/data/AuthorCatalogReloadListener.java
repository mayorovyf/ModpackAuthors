package com.modpackauthors.data;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class AuthorCatalogReloadListener extends SimplePreparableReloadListener<AuthorCatalog> {
    @Override
    protected AuthorCatalog prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return AuthorCatalogLoader.load(resourceManager);
    }

    @Override
    protected void apply(AuthorCatalog catalog, ResourceManager resourceManager, ProfilerFiller profiler) {
        AuthorCatalogLoader.setCachedCatalog(catalog);
    }
}
