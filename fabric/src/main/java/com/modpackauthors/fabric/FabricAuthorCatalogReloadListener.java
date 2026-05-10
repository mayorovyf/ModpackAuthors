package com.modpackauthors.fabric;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.data.AuthorCatalogReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public final class FabricAuthorCatalogReloadListener extends AuthorCatalogReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return ModpackAuthors.id("author_catalog");
    }
}
