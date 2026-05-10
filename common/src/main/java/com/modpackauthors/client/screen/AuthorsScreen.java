package com.modpackauthors.client.screen;

import com.modpackauthors.data.AuthorCatalog;
import com.modpackauthors.data.AuthorCatalogLoader;
import com.modpackauthors.data.AuthorProfile;
import com.modpackauthors.search.AuthorSearch;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public final class AuthorsScreen extends Screen {
    private static final int HEADER_HEIGHT = 46;
    private static final int SIDE_MARGIN = 16;
    private static final int MAX_CONTENT_WIDTH = 760;

    private final Screen parentScreen;
    private AuthorCatalog catalog = AuthorCatalog.empty();
    private EditBox searchBox;
    private AuthorListWidget authorList;
    private String query = "";

    public AuthorsScreen(Screen parentScreen) {
        super(Component.translatable("screen.modpack_authors.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        this.catalog = AuthorCatalogLoader.getCatalog(Minecraft.getInstance().getResourceManager());

        int contentLeft = contentLeft();
        int contentRight = contentRight();
        int backWidth = 74;
        int searchMaxWidth = Math.min(240, Math.max(60, contentWidth() - backWidth - 8));
        int searchWidth = Mth.clamp(contentWidth() / 3, Math.min(120, searchMaxWidth), searchMaxWidth);
        int controlY = 16;
        int backX = contentRight - backWidth;
        int searchX = Math.max(contentLeft, backX - 8 - searchWidth);

        this.searchBox = new EditBox(this.font, searchX, controlY, searchWidth, 20, Component.translatable("screen.modpack_authors.search"));
        this.searchBox.setMaxLength(128);
        this.searchBox.setValue(this.query);
        this.searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.searchBox);

        this.addRenderableWidget(new Button(backX, controlY, backWidth, 20,
                Component.translatable("screen.modpack_authors.back"), button -> onClose()));

        int listTop = HEADER_HEIGHT;
        int listBottom = this.height - SIDE_MARGIN;
        this.authorList = new AuthorListWidget(this.minecraft, this.width, this.height, listTop, listBottom, 60, this::openDetails);
        this.addRenderableWidget(this.authorList);
        applyFilter(false);
    }

    private void onSearchChanged(String value) {
        this.query = value;
        applyFilter(true);
    }

    private void applyFilter(boolean resetScroll) {
        if (this.authorList == null) {
            return;
        }

        List<AuthorProfile> filteredAuthors = AuthorSearch.filter(this.catalog.authors(), this.query);
        this.authorList.setAuthors(filteredAuthors);
        if (resetScroll) {
            this.authorList.resetScroll();
        }
    }

    private void openDetails(AuthorProfile author) {
        if (this.minecraft != null) {
            this.minecraft.setScreen(new AuthorDetailsScreen(this, author));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        this.font.draw(poseStack, Component.translatable(this.catalog.titleKey()), contentLeft(), 22, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderEmptyState(poseStack);
    }

    private void renderEmptyState(PoseStack poseStack) {
        if (this.catalog.authors().isEmpty()) {
            drawCenteredString(poseStack, this.font, Component.translatable("screen.modpack_authors.no_authors"),
                    this.width / 2, this.height / 2, 0xA0A0A0);
        } else if (this.authorList != null && this.authorList.isEmpty()) {
            drawCenteredString(poseStack, this.font, Component.translatable("screen.modpack_authors.no_results"),
                    this.width / 2, this.height / 2, 0xA0A0A0);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parentScreen);
        }
    }

    private int contentWidth() {
        return Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
    }

    private int contentLeft() {
        return (this.width - contentWidth()) / 2;
    }

    private int contentRight() {
        return contentLeft() + contentWidth();
    }
}
