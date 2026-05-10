package com.modpackauthors.forge;

import com.modpackauthors.platform.PlatformServices;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ForgePlatformServices implements PlatformServices {
    @Override
    public String loaderName() {
        return "forge";
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
