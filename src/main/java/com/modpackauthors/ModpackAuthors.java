package com.modpackauthors;

import com.modpackauthors.config.AuthorsClientConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ModpackAuthors.MOD_ID)
public final class ModpackAuthors {
    public static final String MOD_ID = "modpack_authors";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ModpackAuthors(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, AuthorsClientConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
