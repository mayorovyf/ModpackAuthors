package com.modpackauthors.search;

import com.modpackauthors.data.AuthorProfile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class AuthorSearch {
    private AuthorSearch() {
    }

    public static List<AuthorProfile> filter(Collection<AuthorProfile> authors, String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return List.copyOf(authors);
        }

        return authors.stream()
                .filter(author -> matches(author, normalizedQuery))
                .toList();
    }

    public static boolean matches(AuthorProfile author, String normalizedQuery) {
        return contains(author.displayName(), normalizedQuery)
                || contains(author.role(), normalizedQuery)
                || contains(author.listRole(), normalizedQuery)
                || contains(author.shortDescription(), normalizedQuery)
                || contains(author.longDescription(), normalizedQuery)
                || author.tags().stream().anyMatch(value -> contains(value, normalizedQuery))
                || author.contributions().stream().anyMatch(value -> contains(value, normalizedQuery))
                || author.contacts().stream().anyMatch(value -> contains(value, normalizedQuery))
                || author.versions().stream().anyMatch(value -> contains(value, normalizedQuery));
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private static boolean contains(String value, String normalizedQuery) {
        return normalize(value).contains(normalizedQuery);
    }
}
