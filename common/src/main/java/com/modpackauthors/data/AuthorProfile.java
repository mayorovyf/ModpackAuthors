package com.modpackauthors.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AuthorProfile(
        String id,
        LocalizedText displayNameText,
        LocalizedText roleText,
        LocalizedText listRoleText,
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
    public AuthorProfile {
        id = id == null ? "" : id;
        displayNameText = displayNameText == null ? LocalizedText.of("") : displayNameText;
        roleText = roleText == null ? LocalizedText.of("") : roleText;
        listRoleText = listRoleText == null ? roleText : listRoleText;
        shortDescriptionText = shortDescriptionText == null ? LocalizedText.of("") : shortDescriptionText;
        longDescriptionText = longDescriptionText == null ? LocalizedText.of("") : longDescriptionText;
        tagTexts = List.copyOf(tagTexts == null ? List.of() : tagTexts);
        badgeTexts = List.copyOf(badgeTexts == null ? List.of() : badgeTexts);
        contributionTexts = List.copyOf(contributionTexts == null ? List.of() : contributionTexts);
        contactTexts = List.copyOf(contactTexts == null ? List.of() : contactTexts);
        versions = List.copyOf(versions == null ? List.of() : versions);
        links = List.copyOf(links == null ? List.of() : links);
    }

    public String displayName() {
        return this.displayNameText.resolve();
    }

    public String role() {
        return this.roleText.resolve();
    }

    public String listRole() {
        String resolved = this.listRoleText.resolve();
        return resolved.isBlank() ? this.role() : resolved;
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
        return values.stream()
                .map(LocalizedText::resolve)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
