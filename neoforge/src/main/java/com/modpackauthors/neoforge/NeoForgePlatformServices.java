package com.modpackauthors.neoforge;

import com.modpackauthors.platform.PlatformServices;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class NeoForgePlatformServices implements PlatformServices {
    @Override
    public String loaderName() {
        return "neoforge";
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
