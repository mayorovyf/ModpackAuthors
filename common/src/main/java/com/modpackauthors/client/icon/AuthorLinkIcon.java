package com.modpackauthors.client.icon;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class AuthorLinkIcon {
    private final ResourceLocation texture;
    private final Component label;

    public AuthorLinkIcon(ResourceLocation texture, Component label) {
        this.texture = texture;
        this.label = label;
    }

    public ResourceLocation texture() {
        return this.texture;
    }

    public Component label() {
        return this.label;
    }
}
