package com.modpackauthors.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class IconButton extends Button {
    private static final int ICON_SIZE = 16;

    private final ResourceLocation icon;

    public IconButton(ResourceLocation icon, Component label, OnPress onPress) {
        super(0, 0, 24, 24, Component.empty(), onPress, DEFAULT_NARRATION);
        this.icon = icon;
        this.setTooltip(Tooltip.create(label));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);

        int iconX = this.getX() + (this.getWidth() - ICON_SIZE) / 2;
        int iconY = this.getY() + (this.getHeight() - ICON_SIZE) / 2;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(this.icon, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        RenderSystem.disableBlend();
    }
}
