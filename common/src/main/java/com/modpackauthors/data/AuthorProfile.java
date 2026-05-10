package com.modpackauthors.data;

import com.modpackauthors.util.JavaCompat;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class AuthorProfile {
    private final String id;
    private final LocalizedText displayNameText;
    private final LocalizedText roleText;
    private final LocalizedText shortDescriptionText;
    private final LocalizedText longDescriptionText;
    private final ResourceLocation avatarTexture;
    private final int order;
    private final List<LocalizedText> tagTexts;
    private final List<LocalizedText> badgeTexts;
    private final List<LocalizedText> contributionTexts;
    private final List<LocalizedText> contactTexts;
    private final List<String> versions;
    private final List<AuthorLink> links;

    public AuthorProfile(
            String id,
            LocalizedText displayNameText,
            LocalizedText roleText,
            LocalizedText shortDescriptionText,
            LocalizedText longDescriptionText,
            ResourceLocation avatarTexture,
            int order,
            List<LocalizedText> tagTexts,
            List<LocalizedText> badgeTexts,
            List<LocalizedText> contributionTexts,
            List<LocalizedText> contactTexts,
            List<String> versions,
            List<AuthorLink> links
    ) {
        this.id = id == null ? "" : id;
        this.displayNameText = displayNameText == null ? LocalizedText.of("") : displayNameText;
        this.roleText = roleText == null ? LocalizedText.of("") : roleText;
        this.shortDescriptionText = shortDescriptionText == null ? LocalizedText.of("") : shortDescriptionText;
        this.longDescriptionText = longDescriptionText == null ? LocalizedText.of("") : longDescriptionText;
        this.avatarTexture = avatarTexture;
        this.order = order;
        this.tagTexts = JavaCompat.immutableList(tagTexts);
        this.badgeTexts = JavaCompat.immutableList(badgeTexts);
        this.contributionTexts = JavaCompat.immutableList(contributionTexts);
        this.contactTexts = JavaCompat.immutableList(contactTexts);
        this.versions = JavaCompat.immutableList(versions);
        this.links = JavaCompat.immutableList(links);
    }

    public String id() {
        return this.id;
    }

    public LocalizedText displayNameText() {
        return this.displayNameText;
    }

    public LocalizedText roleText() {
        return this.roleText;
    }

    public LocalizedText shortDescriptionText() {
        return this.shortDescriptionText;
    }

    public LocalizedText longDescriptionText() {
        return this.longDescriptionText;
    }

    public ResourceLocation avatarTexture() {
        return this.avatarTexture;
    }

    public int order() {
        return this.order;
    }

    public List<LocalizedText> tagTexts() {
        return this.tagTexts;
    }

    public List<LocalizedText> badgeTexts() {
        return this.badgeTexts;
    }

    public List<LocalizedText> contributionTexts() {
        return this.contributionTexts;
    }

    public List<LocalizedText> contactTexts() {
        return this.contactTexts;
    }

    public List<String> versions() {
        return this.versions;
    }

    public List<AuthorLink> links() {
        return this.links;
    }

    public String displayName() {
        return this.displayNameText.resolve();
    }

    public String role() {
        return this.roleText.resolve();
    }

    public String shortDescription() {
        return this.shortDescriptionText.resolve();
    }

    public String longDescription() {
        return this.longDescriptionText.resolve();
    }

    public List<String> tags() {
        return resolveList(this.tagTexts);
    }

    public List<String> badges() {
        return resolveList(this.badgeTexts);
    }

    public List<String> contributions() {
        return resolveList(this.contributionTexts);
    }

    public List<String> contacts() {
        return resolveList(this.contactTexts);
    }

    private static List<String> resolveList(List<LocalizedText> values) {
        List<String> resolved = new ArrayList<String>();
        for (LocalizedText value : values) {
            String text = value.resolve();
            if (!JavaCompat.isBlank(text)) {
                resolved.add(text);
            }
        }
        return JavaCompat.immutableList(resolved);
    }
}
