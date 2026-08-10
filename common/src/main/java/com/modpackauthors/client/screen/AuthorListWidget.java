package com.modpackauthors.client.screen;

import com.modpackauthors.client.icon.AuthorLinkIcon;
import com.modpackauthors.client.icon.AuthorLinkIcons;
import com.modpackauthors.client.widget.IconButton;
import com.modpackauthors.data.AuthorLink;
import com.modpackauthors.data.AuthorProfile;
import com.modpackauthors.util.TextUtil;
import com.modpackauthors.util.UrlOpenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public final class AuthorListWidget extends ObjectSelectionList<AuthorListWidget.AuthorEntry> {
    private static final int SIDE_MARGIN = 16;
    private static final int MAX_CONTENT_WIDTH = 760;

    private final AuthorsScreen owner;
    private final Consumer<AuthorProfile> detailsAction;

    public AuthorListWidget(Minecraft minecraft, int width, int height, int top, int bottom, int rowHeight,
                            Consumer<AuthorProfile> detailsAction) {
        super(minecraft, width, height, top, bottom, rowHeight);
        this.owner = (AuthorsScreen) minecraft.screen;
        this.detailsAction = detailsAction;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    public void setAuthors(List<AuthorProfile> authors) {
        this.clearEntries();
        for (AuthorProfile author : authors) {
            this.addEntry(new AuthorEntry(this.owner, author, this.detailsAction));
        }
    }

    public void resetScroll() {
        this.setScrollAmount(0.0D);
    }

    public boolean isEmpty() {
        return this.getItemCount() == 0;
    }

    @Override
    public int getRowWidth() {
        return Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
    }

    @Override
    public int getRowLeft() {
        return (this.width - this.getRowWidth()) / 2;
    }

    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRowRight() + 8;
    }

    public static final class AuthorEntry extends ObjectSelectionList.Entry<AuthorEntry> {
        private static final int AVATAR_SIZE = 40;
        private static final int ACTION_BUTTON_SIZE = 24;
        private static final int ACTION_BUTTON_GAP = 6;
        private static final int TEXT_LINE_HEIGHT = 13;

        private final AuthorsScreen owner;
        private final AuthorProfile author;
        private final Consumer<AuthorProfile> detailsAction;
        private final List<IconButton> actionButtons;

        private AuthorEntry(AuthorsScreen owner, AuthorProfile author, Consumer<AuthorProfile> detailsAction) {
            this.owner = owner;
            this.author = author;
            this.detailsAction = detailsAction;

            List<IconButton> buttons = new java.util.ArrayList<>();
            for (AuthorLink link : author.links()) {
                AuthorLinkIcon icon = AuthorLinkIcons.iconFor(link);
                buttons.add(new IconButton(icon.texture(), icon.label(), button -> UrlOpenHelper.confirmOpen(this.owner, link.url())));
            }
            buttons.add(new IconButton(AuthorLinkIcons.DETAILS,
                    Component.translatable("screen.modpack_authors.details"),
                    button -> detailsAction.accept(author)));
            this.actionButtons = List.copyOf(buttons);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int rowWidth, int rowHeight,
                           int mouseX, int mouseY, boolean hovered, float partialTick) {
            int height = rowHeight - 4;
            int background = hovered ? 0x663F4A52 : 0x55303030;
            int border = hovered ? 0xFFA0A0A0 : 0xFF606060;

            graphics.fill(left, top, left + rowWidth, top + height, background);
            graphics.fill(left, top, left + rowWidth, top + 1, border);
            graphics.fill(left, top + height - 1, left + rowWidth, top + height, border);
            graphics.fill(left, top, left + 1, top + height, border);
            graphics.fill(left + rowWidth - 1, top, left + rowWidth, top + height, border);

            int avatarX = left + 8;
            int avatarY = top + (height - AVATAR_SIZE) / 2;
            graphics.fill(avatarX - 1, avatarY - 1, avatarX + AVATAR_SIZE + 1, avatarY + AVATAR_SIZE + 1, 0xFF202020);
            graphics.blit(this.author.avatarTexture(), avatarX, avatarY, 0, 0, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE);

            Font font = Minecraft.getInstance().font;
            int textX = avatarX + AVATAR_SIZE + 10;
            int actionsWidth = this.actionButtons.size() * ACTION_BUTTON_SIZE
                    + Math.max(0, this.actionButtons.size() - 1) * ACTION_BUTTON_GAP;
            int actionsX = left + rowWidth - actionsWidth - 8;
            int actionsY = top + (height - ACTION_BUTTON_SIZE) / 2;
            int textWidth = Math.max(40, actionsX - textX - 10);
            String listRole = this.author.listRole();
            int textLineCount = 1;
            if (!listRole.isBlank()) {
                textLineCount++;
            }
            if (!this.author.shortDescription().isBlank()) {
                textLineCount++;
            }
            int textBlockHeight = (textLineCount - 1) * TEXT_LINE_HEIGHT + font.lineHeight;
            int textY = top + (height - textBlockHeight) / 2 + 2;

            graphics.drawString(font, TextUtil.ellipsize(font, this.author.displayName(), textWidth), textX, textY, 0xFFFFFF, false);
            if (!listRole.isBlank()) {
                graphics.drawString(font, TextUtil.ellipsize(font, listRole, textWidth), textX, textY + TEXT_LINE_HEIGHT, 0xD7D7D7, false);
            }
            if (!this.author.shortDescription().isBlank()) {
                int descriptionY = textY + (textLineCount == 3 ? TEXT_LINE_HEIGHT * 2 : TEXT_LINE_HEIGHT);
                graphics.drawString(font, TextUtil.ellipsize(font, this.author.shortDescription(), textWidth), textX, descriptionY, 0xA8A8A8, false);
            }

            int buttonX = actionsX;
            for (IconButton button : this.actionButtons) {
                button.setX(buttonX);
                button.setY(actionsY);
                button.render(graphics, mouseX, mouseY, partialTick);
                buttonX += ACTION_BUTTON_SIZE + ACTION_BUTTON_GAP;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (IconButton actionButton : this.actionButtons) {
                if (actionButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            if (button == 0) {
                this.detailsAction.accept(this.author);
                return true;
            }
            return false;
        }

        public List<? extends GuiEventListener> children() {
            return this.actionButtons;
        }

        public List<? extends NarratableEntry> narratables() {
            return this.actionButtons;
        }

        @Override
        public Component getNarration() {
            return Component.literal(this.author.displayName());
        }
    }
}
