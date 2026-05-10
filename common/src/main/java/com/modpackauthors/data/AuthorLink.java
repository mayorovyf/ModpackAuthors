package com.modpackauthors.data;

public final class AuthorLink {
    private final String label;
    private final String url;

    public AuthorLink(String label, String url) {
        this.label = label == null ? "" : label;
        this.url = url == null ? "" : url;
    }

    public String label() {
        return this.label;
    }

    public String url() {
        return this.url;
    }
}
