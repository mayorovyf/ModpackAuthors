package com.modpackauthors.fabric;

import com.modpackauthors.platform.PlatformServices;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class FabricPlatformServices implements PlatformServices {
    @Override
    public String loaderName() {
        return "fabric";
    }

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
