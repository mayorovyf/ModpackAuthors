package com.modpackauthors.data;

import com.modpackauthors.util.JavaCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class AuthorCatalog {
    private static final String DEFAULT_TITLE_KEY = "screen.modpack_authors.title";

    private final int schemaVersion;
    private final String titleKey;
    private final List<AuthorProfile> authors;

    public AuthorCatalog(int schemaVersion, String titleKey, List<AuthorProfile> authors) {
        this.schemaVersion = schemaVersion;
        this.titleKey = JavaCompat.isBlank(titleKey) ? DEFAULT_TITLE_KEY : titleKey;
        this.authors = JavaCompat.immutableList(authors);
    }

    public int schemaVersion() {
        return this.schemaVersion;
    }

    public String titleKey() {
        return this.titleKey;
    }

    public List<AuthorProfile> authors() {
        return this.authors;
    }

    public static AuthorCatalog empty() {
        return new AuthorCatalog(1, DEFAULT_TITLE_KEY, Collections.<AuthorProfile>emptyList());
    }

    public AuthorCatalog sorted() {
        List<AuthorProfile> sortedAuthors = new ArrayList<AuthorProfile>(this.authors);
        Collections.sort(sortedAuthors, Comparator.comparingInt(AuthorProfile::order)
                .thenComparing(AuthorProfile::displayName, String.CASE_INSENSITIVE_ORDER));
        return new AuthorCatalog(this.schemaVersion, this.titleKey, sortedAuthors);
    }
}
