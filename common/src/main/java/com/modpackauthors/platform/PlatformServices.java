package com.modpackauthors.platform;

import java.nio.file.Path;

public interface PlatformServices {
    String loaderName();

    Path configDir();
}
