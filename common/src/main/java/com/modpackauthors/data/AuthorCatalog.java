package com.modpackauthors.data;

import java.util.Comparator;
import java.util.List;

public record AuthorCatalog(int schemaVersion, String titleKey, List<AuthorProfile> authors) {
    private static final String DEFAULT_TITLE_KEY = "screen.modpack_authors.title";

    public AuthorCatalog {
        titleKey = titleKey == null || titleKey.isBlank() ? DEFAULT_TITLE_KEY : titleKey;
        authors = List.copyOf(authors == null ? List.of() : authors);
    }

    public static AuthorCatalog empty() {
        return new AuthorCatalog(1, DEFAULT_TITLE_KEY, List.of());
    }

    public AuthorCatalog sorted() {
        return new AuthorCatalog(schemaVersion, titleKey, authors.stream()
                .sorted(Comparator.comparingInt(AuthorProfile::order)
                        .thenComparing(AuthorProfile::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList());
    }
}
