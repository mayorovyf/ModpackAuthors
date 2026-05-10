package com.modpackauthors.client.widget;

import com.modpackauthors.util.Components;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class IconButton extends Button {
    private static final int ICON_SIZE = 16;

    private final ResourceLocation icon;
    private final Component label;

    public IconButton(ResourceLocation icon, Component label, OnPress onPress) {
        super(0, 0, 24, 24, Components.empty(), onPress);
        this.icon = icon;
        this.label = label;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int iconX = this.x + (this.width - ICON_SIZE) / 2;
        int iconY = this.y + (this.height - ICON_SIZE) / 2;
        Minecraft.getInstance().getTextureManager().bind(this.icon);
        GuiComponent.blit(poseStack, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        if (this.isHovered()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.renderTooltip(poseStack, this.label, mouseX, mouseY);
            }
        }
    }
}
