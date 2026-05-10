package com.modpackauthors;

import com.modpackauthors.config.AuthorsClientConfig;
import com.modpackauthors.platform.PlatformServices;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModpackAuthors {
    public static final String MOD_ID = "modpack_authors";
    public static final Logger LOGGER = LogManager.getLogger();

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
        try {
            return new ResourceLocation(MOD_ID, path);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid mod resource path: " + path);
        }
    }
}
