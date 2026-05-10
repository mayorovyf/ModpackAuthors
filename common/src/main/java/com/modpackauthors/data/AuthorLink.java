package com.modpackauthors.data;

public record AuthorLink(String label, String url) {
    public AuthorLink {
        label = label == null ? "" : label;
        url = url == null ? "" : url;
    }
}
