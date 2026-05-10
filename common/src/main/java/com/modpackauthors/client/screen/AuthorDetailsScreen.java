package com.modpackauthors.client.screen;

import com.modpackauthors.client.icon.AuthorLinkIcon;
import com.modpackauthors.client.icon.AuthorLinkIcons;
import com.modpackauthors.data.AuthorLink;
import com.modpackauthors.data.AuthorProfile;
import com.modpackauthors.util.Components;
import com.modpackauthors.util.UrlOpenHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public final class AuthorDetailsScreen extends Screen {
    private static final int MARGIN = 18;
    private static final int PANEL_MAX_WIDTH = 760;
    private static final int PANEL_TOP = 58;
    private static final int AVATAR_SIZE = 64;
    private static final int CONTENT_PADDING = 18;
    private static final int SECTION_GAP = 16;
    private static final int TEXT_LINE_HEIGHT = 11;
    private static final int CONTACT_ROW_HEIGHT = 22;
    private static final int CONTACT_ICON_SLOT = 18;
    private static final int CONTACT_ICON_SIZE = 14;

    private final Screen parentScreen;
    private final AuthorProfile author;
    private final List<LinkHitbox> linkHitboxes = new ArrayList<>();
    private double scrollAmount;

    public AuthorDetailsScreen(Screen parentScreen, AuthorProfile author) {
        super(Components.literal(author.displayName()));
        this.parentScreen = parentScreen;
        this.author = author;
    }

    @Override
    protected void init() {
        int panelRight = panelLeft() + panelWidth();
        this.addRenderableWidget(new Button(panelRight - 74, 18, 74, 20,
                Components.translatable("screen.modpack_authors.back"), button -> onClose()));

        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, maxScroll());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        renderHeader(poseStack);
        renderPanel(poseStack, mouseX, mouseY);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    private void renderHeader(PoseStack poseStack) {
        this.font.draw(poseStack,
                Components.literal(Components.translatable("screen.modpack_authors.title").getString() + " > " + this.author.displayName()),
                panelLeft(), 28, 0xFFFFFF);
    }

    private void renderPanel(PoseStack poseStack, int mouseX, int mouseY) {
        int panelLeft = panelLeft();
        int panelWidth = panelWidth();
        int panelBottom = this.height - 26;
        int panelHeight = panelBottom - PANEL_TOP;

        GuiComponent.fill(poseStack, panelLeft, PANEL_TOP, panelLeft + panelWidth, panelBottom, 0x66303030);
        drawBorder(poseStack, panelLeft, PANEL_TOP, panelWidth, panelHeight);

        this.linkHitboxes.clear();
        enableScissor(panelLeft + 2, PANEL_TOP + 2, panelLeft + panelWidth - 2, panelBottom - 2);
        try {
            layoutContent(poseStack, panelLeft, PANEL_TOP, panelWidth, true, mouseX, mouseY);
        } finally {
            RenderSystem.disableScissor();
        }

        renderScrollHint(poseStack, panelLeft, PANEL_TOP, panelWidth, panelHeight);
    }

    private int layoutContent(PoseStack poseStack, int panelLeft, int panelTop, int panelWidth, boolean render, int mouseX, int mouseY) {
        int contentX = panelLeft + CONTENT_PADDING;
        int contentWidth = panelWidth - CONTENT_PADDING * 2;
        int cursorY = panelTop + CONTENT_PADDING - (render ? (int) this.scrollAmount : 0);

        cursorY = renderSummary(poseStack, contentX, cursorY, contentWidth, render);
        cursorY = separator(poseStack, contentX, cursorY + SECTION_GAP, contentWidth, render);

        cursorY = renderAbout(poseStack, contentX, cursorY, contentWidth, render);

        List<String> roles = this.author.badges().isEmpty() ? this.author.tags() : this.author.badges();
        if (!roles.isEmpty()) {
            cursorY = separator(poseStack, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderChipSection(poseStack, Components.translatable("screen.modpack_authors.project_roles"), roles,
                    contentX, cursorY, contentWidth, render);
        }

        if (!this.author.links().isEmpty() || !this.author.contacts().isEmpty()) {
            cursorY = separator(poseStack, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderContacts(poseStack, contentX, cursorY, contentWidth, render, mouseX, mouseY);
        }

        if (!this.author.contributions().isEmpty()) {
            cursorY = separator(poseStack, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderBulletSection(poseStack, Components.translatable("screen.modpack_authors.contributions"),
                    this.author.contributions(), contentX, cursorY, contentWidth, render);
        }

        if (!this.author.versions().isEmpty()) {
            cursorY = separator(poseStack, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderBulletSection(poseStack, Components.translatable("screen.modpack_authors.versions"),
                    this.author.versions(), contentX, cursorY, contentWidth, render);
        }

        return cursorY + CONTENT_PADDING;
    }

    private int renderSummary(PoseStack poseStack, int x, int y, int width, boolean render) {
        int textX = x + AVATAR_SIZE + 18;
        if (render) {
            GuiComponent.fill(poseStack, x - 1, y - 1, x + AVATAR_SIZE + 1, y + AVATAR_SIZE + 1, 0xFF202020);
            blitTexture(poseStack, this.author.avatarTexture(), x, y, AVATAR_SIZE);
            this.font.draw(poseStack, Components.literal(this.author.displayName()), textX, y + 8, 0xFFFFFF);
            if (!this.author.role().isBlank()) {
                this.font.draw(poseStack, Components.literal(this.author.role()), textX, y + 24, 0xD7D7D7);
            }
            if (!this.author.shortDescription().isBlank()) {
                this.font.draw(poseStack, Components.literal(this.author.shortDescription()), textX, y + 46, 0xC9C9C9);
            }
        }
        return y + AVATAR_SIZE;
    }

    private int renderAbout(PoseStack poseStack, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(poseStack, Components.translatable("screen.modpack_authors.about_author"), x, y, render);
        String description = this.author.longDescription().isBlank()
                ? this.author.shortDescription()
                : this.author.longDescription();
        if (!description.isBlank()) {
            cursorY = renderWrappedText(poseStack, description, x, cursorY, width, 0xDADADA, render);
        }
        return cursorY;
    }

    private int renderChipSection(PoseStack poseStack, Component title, List<String> chips, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(poseStack, title, x, y, render);
        int chipX = x;
        int chipY = cursorY;
        int chipHeight = 18;

        for (String chip : chips) {
            int chipWidth = this.font.width(chip) + 14;
            if (chipX > x && chipX + chipWidth > x + width) {
                chipX = x;
                chipY += chipHeight + 5;
            }

            if (render) {
                GuiComponent.fill(poseStack, chipX, chipY, chipX + chipWidth, chipY + chipHeight, 0x88404040);
                drawBorder(poseStack, chipX, chipY, chipWidth, chipHeight);
                this.font.draw(poseStack, Components.literal(chip), chipX + 7, chipY + 5, 0xDADADA);
            }
            chipX += chipWidth + 6;
        }

        return chipY + chipHeight;
    }

    private int renderContacts(PoseStack poseStack, int x, int y, int width, boolean render, int mouseX, int mouseY) {
        int cursorY = renderSectionTitle(poseStack, Components.translatable("screen.modpack_authors.contacts"), x, y, render);
        for (AuthorLink link : this.author.links()) {
            AuthorLinkIcon icon = AuthorLinkIcons.iconFor(link);
            int rowTop = cursorY - 2;
            int rowBottom = cursorY + CONTACT_ROW_HEIGHT - 2;
            boolean visible = rowBottom >= PANEL_TOP && rowTop <= this.height - 26;
            boolean hovered = visible && mouseX >= x && mouseX <= x + width && mouseY >= rowTop && mouseY <= rowBottom;

            if (render) {
                if (hovered) {
                    GuiComponent.fill(poseStack, x - 4, rowTop, x + width, rowBottom, 0x553F4A52);
                }
                int iconX = x + (CONTACT_ICON_SLOT - CONTACT_ICON_SIZE) / 2;
                int iconY = rowTop + (CONTACT_ROW_HEIGHT - CONTACT_ICON_SIZE) / 2;
                blitTexture(poseStack, icon.texture(), iconX, iconY, CONTACT_ICON_SIZE);
                this.font.draw(poseStack, icon.label(), x + CONTACT_ICON_SLOT + 8, cursorY + 6, hovered ? 0xFFFFFF : 0xDADADA);
                if (visible) {
                    this.linkHitboxes.add(new LinkHitbox(x - 4, Math.max(rowTop, PANEL_TOP + 2),
                            x + width, Math.min(rowBottom, this.height - 28), link));
                }
            }

            cursorY += CONTACT_ROW_HEIGHT;
        }
        for (String contact : this.author.contacts()) {
            cursorY = renderWrappedText(poseStack, "- " + contact, x, cursorY + 2, width, 0xC9C9C9, render);
            cursorY += 2;
        }
        return cursorY;
    }

    private int renderBulletSection(PoseStack poseStack, Component title, List<String> values, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(poseStack, title, x, y, render);
        for (String value : values) {
            cursorY = renderWrappedText(poseStack, "- " + value, x, cursorY, width, 0xC9C9C9, render);
            cursorY += 2;
        }
        return cursorY;
    }

    private int renderSectionTitle(PoseStack poseStack, Component title, int x, int y, boolean render) {
        if (render) {
            this.font.draw(poseStack, title, x, y, 0xFFFFFF);
        }
        return y + 18;
    }

    private int renderWrappedText(PoseStack poseStack, String text, int x, int y, int width, int color, boolean render) {
        int cursorY = y;
        for (var line : this.font.split(Components.literal(text), width)) {
            if (render) {
                this.font.draw(poseStack, line, x, cursorY, color);
            }
            cursorY += TEXT_LINE_HEIGHT;
        }
        return cursorY;
    }

    private int separator(PoseStack poseStack, int x, int y, int width, boolean render) {
        if (render) {
            GuiComponent.fill(poseStack, x, y, x + width, y + 1, 0xFF606060);
        }
        return y + SECTION_GAP;
    }

    private void drawBorder(PoseStack poseStack, int x, int y, int width, int height) {
        GuiComponent.fill(poseStack, x, y, x + width, y + 1, 0xFF707070);
        GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, 0xFF707070);
        GuiComponent.fill(poseStack, x, y, x + 1, y + height, 0xFF707070);
        GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, 0xFF707070);
    }

    private void renderScrollHint(PoseStack poseStack, int panelLeft, int panelTop, int panelWidth, int panelHeight) {
        double maxScroll = maxScroll();
        if (maxScroll <= 0.0D) {
            return;
        }

        int trackX = panelLeft + panelWidth - 8;
        int trackTop = panelTop + 8;
        int trackHeight = panelHeight - 16;
        int thumbHeight = Math.max(20, (int) (trackHeight * (trackHeight / (double) (trackHeight + maxScroll))));
        int thumbY = trackTop + (int) ((trackHeight - thumbHeight) * (this.scrollAmount / maxScroll));

        GuiComponent.fill(poseStack, trackX, trackTop, trackX + 3, trackTop + trackHeight, 0x77303030);
        GuiComponent.fill(poseStack, trackX, thumbY, trackX + 3, thumbY + thumbHeight, 0xFFB0B0B0);
    }

    private static void blitTexture(PoseStack poseStack, ResourceLocation texture, int x, int y, int size) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(poseStack, x, y, 0, 0, size, size, size, size);
        RenderSystem.disableBlend();
    }

    private static void enableScissor(int left, int top, int right, int bottom) {
        Minecraft minecraft = Minecraft.getInstance();
        double scale = minecraft.getWindow().getGuiScale();
        int scissorX = (int) Math.floor(left * scale);
        int scissorY = (int) Math.floor((minecraft.getWindow().getGuiScaledHeight() - bottom) * scale);
        int scissorWidth = (int) Math.ceil((right - left) * scale);
        int scissorHeight = (int) Math.ceil((bottom - top) * scale);
        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (LinkHitbox hitbox : this.linkHitboxes) {
                if (hitbox.contains(mouseX, mouseY)) {
                    UrlOpenHelper.confirmOpen(this, hitbox.link().url());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        double maxScroll = maxScroll();
        if (maxScroll <= 0.0D) {
            return false;
        }

        this.scrollAmount = Mth.clamp(this.scrollAmount - delta * 18.0D, 0.0D, maxScroll);
        return true;
    }

    private double maxScroll() {
        int panelWidth = panelWidth();
        int contentHeight = layoutContent(null, panelLeft(), PANEL_TOP, panelWidth, false, 0, 0);
        int availableHeight = Math.max(0, this.height - 26 - PANEL_TOP);
        return Math.max(0, contentHeight - PANEL_TOP - availableHeight);
    }

    private int panelWidth() {
        return Math.min(PANEL_MAX_WIDTH, this.width - MARGIN * 2);
    }

    private int panelLeft() {
        return (this.width - panelWidth()) / 2;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parentScreen);
        }
    }

    private record LinkHitbox(int x1, int y1, int x2, int y2, AuthorLink link) {
        private boolean contains(double x, double y) {
            return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2;
        }
    }
}
