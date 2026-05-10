package com.modpackauthors;

import com.modpackauthors.config.AuthorsClientConfig;
import com.modpackauthors.platform.PlatformServices;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class ModpackAuthors {
    public static final String MOD_ID = "modpack_authors";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static PlatformServices platform;

    private ModpackAuthors() {
    }

    public static void init(PlatformServices platformServices) {
        platform = platformServices;
        AuthorsClientConfig.load(platformServices.configDir());
    }

    public static PlatformServices platform() {
        if (platform == null) {
            throw new IllegalStateException("Modpack Authors platform services were not initialized");
        }

        return platform;
    }

    public static ResourceLocation id(String path) {
        ResourceLocation location = ResourceLocation.tryBuild(MOD_ID, path);
        if (location == null) {
            throw new IllegalArgumentException("Invalid mod resource path: " + path);
        }
        return location;
    }
}
