package com.modpackauthors.client.screen;

import com.modpackauthors.client.icon.AuthorLinkIcon;
import com.modpackauthors.client.icon.AuthorLinkIcons;
import com.modpackauthors.data.AuthorLink;
import com.modpackauthors.data.AuthorProfile;
import com.modpackauthors.util.UrlOpenHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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

    private final Screen parentScreen;
    private final AuthorProfile author;
    private final List<LinkHitbox> linkHitboxes = new ArrayList<>();
    private double scrollAmount;

    public AuthorDetailsScreen(Screen parentScreen, AuthorProfile author) {
        super(Component.literal(author.displayName()));
        this.parentScreen = parentScreen;
        this.author = author;
    }

    @Override
    protected void init() {
        int panelRight = panelLeft() + panelWidth();
        this.addRenderableWidget(Button.builder(Component.translatable("screen.modpack_authors.back"), button -> onClose())
                .bounds(panelRight - 74, 18, 74, 20)
                .build());

        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, maxScroll());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        renderHeader(graphics);
        renderPanel(graphics, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderHeader(GuiGraphics graphics) {
        graphics.drawString(this.font,
                Component.literal(Component.translatable("screen.modpack_authors.title").getString() + " > " + this.author.displayName()),
                panelLeft(), 28, 0xFFFFFF, false);
    }

    private void renderPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelLeft = panelLeft();
        int panelWidth = panelWidth();
        int panelBottom = this.height - 26;
        int panelHeight = panelBottom - PANEL_TOP;

        graphics.fill(panelLeft, PANEL_TOP, panelLeft + panelWidth, panelBottom, 0x66303030);
        drawBorder(graphics, panelLeft, PANEL_TOP, panelWidth, panelHeight);

        this.linkHitboxes.clear();
        graphics.enableScissor(panelLeft + 2, PANEL_TOP + 2, panelLeft + panelWidth - 2, panelBottom - 2);
        layoutContent(graphics, panelLeft, PANEL_TOP, panelWidth, true, mouseX, mouseY);
        graphics.disableScissor();

        renderScrollHint(graphics, panelLeft, PANEL_TOP, panelWidth, panelHeight);
    }

    private int layoutContent(GuiGraphics graphics, int panelLeft, int panelTop, int panelWidth, boolean render, int mouseX, int mouseY) {
        int contentX = panelLeft + CONTENT_PADDING;
        int contentWidth = panelWidth - CONTENT_PADDING * 2;
        int cursorY = panelTop + CONTENT_PADDING - (render ? (int) this.scrollAmount : 0);

        cursorY = renderSummary(graphics, contentX, cursorY, contentWidth, render);
        cursorY = separator(graphics, contentX, cursorY + SECTION_GAP, contentWidth, render);

        cursorY = renderAbout(graphics, contentX, cursorY, contentWidth, render);

        List<String> roles = this.author.badges().isEmpty() ? this.author.tags() : this.author.badges();
        if (!roles.isEmpty()) {
            cursorY = separator(graphics, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderChipSection(graphics, Component.translatable("screen.modpack_authors.project_roles"), roles,
                    contentX, cursorY, contentWidth, render);
        }

        if (!this.author.links().isEmpty()) {
            cursorY = separator(graphics, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderContacts(graphics, contentX, cursorY, contentWidth, render, mouseX, mouseY);
        }

        if (!this.author.contributions().isEmpty()) {
            cursorY = separator(graphics, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderBulletSection(graphics, Component.translatable("screen.modpack_authors.contributions"),
                    this.author.contributions(), contentX, cursorY, contentWidth, render);
        }

        if (!this.author.versions().isEmpty()) {
            cursorY = separator(graphics, contentX, cursorY + SECTION_GAP, contentWidth, render);
            cursorY = renderBulletSection(graphics, Component.translatable("screen.modpack_authors.versions"),
                    this.author.versions(), contentX, cursorY, contentWidth, render);
        }

        return cursorY + CONTENT_PADDING;
    }

    private int renderSummary(GuiGraphics graphics, int x, int y, int width, boolean render) {
        int textX = x + AVATAR_SIZE + 18;
        if (render) {
            graphics.fill(x - 1, y - 1, x + AVATAR_SIZE + 1, y + AVATAR_SIZE + 1, 0xFF202020);
            graphics.blit(this.author.avatarTexture(), x, y, 0, 0, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE);
            graphics.drawString(this.font, Component.literal(this.author.displayName()), textX, y + 8, 0xFFFFFF, false);
            if (!this.author.role().isBlank()) {
                graphics.drawString(this.font, Component.literal(this.author.role()), textX, y + 24, 0xD7D7D7, false);
            }
            if (!this.author.shortDescription().isBlank()) {
                graphics.drawString(this.font, Component.literal(this.author.shortDescription()), textX, y + 46, 0xC9C9C9, false);
            }
        }
        return y + AVATAR_SIZE;
    }

    private int renderAbout(GuiGraphics graphics, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(graphics, Component.translatable("screen.modpack_authors.about_author"), x, y, render);
        String description = this.author.longDescription().isBlank()
                ? this.author.shortDescription()
                : this.author.longDescription();
        if (!description.isBlank()) {
            cursorY = renderWrappedText(graphics, description, x, cursorY, width, 0xDADADA, render);
        }
        return cursorY;
    }

    private int renderChipSection(GuiGraphics graphics, Component title, List<String> chips, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(graphics, title, x, y, render);
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
                graphics.fill(chipX, chipY, chipX + chipWidth, chipY + chipHeight, 0x88404040);
                drawBorder(graphics, chipX, chipY, chipWidth, chipHeight);
                graphics.drawString(this.font, Component.literal(chip), chipX + 7, chipY + 5, 0xDADADA, false);
            }
            chipX += chipWidth + 6;
        }

        return chipY + chipHeight;
    }

    private int renderContacts(GuiGraphics graphics, int x, int y, int width, boolean render, int mouseX, int mouseY) {
        int cursorY = renderSectionTitle(graphics, Component.translatable("screen.modpack_authors.contacts"), x, y, render);
        for (AuthorLink link : this.author.links()) {
            AuthorLinkIcon icon = AuthorLinkIcons.iconFor(link);
            int rowHeight = 22;
            int rowTop = cursorY - 2;
            int rowBottom = cursorY + rowHeight - 2;
            boolean visible = rowBottom >= PANEL_TOP && rowTop <= this.height - 26;
            boolean hovered = visible && mouseX >= x && mouseX <= x + width && mouseY >= rowTop && mouseY <= rowBottom;

            if (render) {
                if (hovered) {
                    graphics.fill(x - 4, rowTop, x + width, rowBottom, 0x553F4A52);
                }
                graphics.blit(icon.texture(), x, cursorY + 2, 0, 0, 16, 16, 16, 16);
                graphics.drawString(this.font, icon.label(), x + 24, cursorY + 6, hovered ? 0xFFFFFF : 0xDADADA, false);
                if (visible) {
                    this.linkHitboxes.add(new LinkHitbox(x - 4, rowTop, x + width, rowBottom, link));
                }
            }

            cursorY += rowHeight;
        }
        return cursorY;
    }

    private int renderBulletSection(GuiGraphics graphics, Component title, List<String> values, int x, int y, int width, boolean render) {
        int cursorY = renderSectionTitle(graphics, title, x, y, render);
        for (String value : values) {
            cursorY = renderWrappedText(graphics, "- " + value, x, cursorY, width, 0xC9C9C9, render);
            cursorY += 2;
        }
        return cursorY;
    }

    private int renderSectionTitle(GuiGraphics graphics, Component title, int x, int y, boolean render) {
        if (render) {
            graphics.drawString(this.font, title, x, y, 0xFFFFFF, false);
        }
        return y + 18;
    }

    private int renderWrappedText(GuiGraphics graphics, String text, int x, int y, int width, int color, boolean render) {
        int cursorY = y;
        for (var line : this.font.split(Component.literal(text), width)) {
            if (render) {
                graphics.drawString(this.font, line, x, cursorY, color, false);
            }
            cursorY += TEXT_LINE_HEIGHT;
        }
        return cursorY;
    }

    private int separator(GuiGraphics graphics, int x, int y, int width, boolean render) {
        if (render) {
            graphics.fill(x, y, x + width, y + 1, 0xFF606060);
        }
        return y + SECTION_GAP;
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + 1, 0xFF707070);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xFF707070);
        graphics.fill(x, y, x + 1, y + height, 0xFF707070);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xFF707070);
    }

    private void renderScrollHint(GuiGraphics graphics, int panelLeft, int panelTop, int panelWidth, int panelHeight) {
        double maxScroll = maxScroll();
        if (maxScroll <= 0.0D) {
            return;
        }

        int trackX = panelLeft + panelWidth - 8;
        int trackTop = panelTop + 8;
        int trackHeight = panelHeight - 16;
        int thumbHeight = Math.max(20, (int) (trackHeight * (trackHeight / (double) (trackHeight + maxScroll))));
        int thumbY = trackTop + (int) ((trackHeight - thumbHeight) * (this.scrollAmount / maxScroll));

        graphics.fill(trackX, trackTop, trackX + 3, trackTop + trackHeight, 0x77303030);
        graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbHeight, 0xFFB0B0B0);
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
