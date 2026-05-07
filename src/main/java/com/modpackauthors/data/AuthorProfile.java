package com.modpackauthors.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AuthorProfile(
        String id,
        String displayName,
        String role,
        String shortDescription,
        String longDescription,
        ResourceLocation avatarTexture,
        int order,
        List<String> tags,
        List<String> badges,
        List<String> contributions,
        List<String> versions,
        List<AuthorLink> links
) {
    public AuthorProfile {
        id = id == null ? "" : id;
        displayName = displayName == null ? "" : displayName;
        role = role == null ? "" : role;
        shortDescription = shortDescription == null ? "" : shortDescription;
        longDescription = longDescription == null ? "" : longDescription;
        tags = List.copyOf(tags == null ? List.of() : tags);
        badges = List.copyOf(badges == null ? List.of() : badges);
        contributions = List.copyOf(contributions == null ? List.of() : contributions);
        versions = List.copyOf(versions == null ? List.of() : versions);
        links = List.copyOf(links == null ? List.of() : links);
    }
}
