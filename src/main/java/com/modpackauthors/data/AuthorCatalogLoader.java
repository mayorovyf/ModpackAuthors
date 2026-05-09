package com.modpackauthors.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.modpackauthors.ModpackAuthors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class AuthorCatalogLoader {
    private static final Gson GSON = new Gson();
    private static final ResourceLocation CATALOG_LOCATION = ModpackAuthors.id("authors/authors.json");
    private static final ResourceLocation FALLBACK_AVATAR = ModpackAuthors.id("textures/gui/authors/default.png");

    private static AuthorCatalog cachedCatalog = AuthorCatalog.empty();
    private static boolean loadedOnce;

    private AuthorCatalogLoader() {
    }

    public static synchronized AuthorCatalog getCatalog(ResourceManager resourceManager) {
        if (!loadedOnce) {
            cachedCatalog = load(resourceManager);
            loadedOnce = true;
        }
        return cachedCatalog;
    }

    public static synchronized void setCachedCatalog(AuthorCatalog catalog) {
        cachedCatalog = catalog == null ? AuthorCatalog.empty() : catalog;
        loadedOnce = true;
    }

    public static AuthorCatalog load(ResourceManager resourceManager) {
        Optional<Resource> resource = resourceManager.getResource(CATALOG_LOCATION);
        if (resource.isEmpty()) {
            ModpackAuthors.LOGGER.warn("Author catalog {} is missing; using empty catalog", CATALOG_LOCATION);
            return AuthorCatalog.empty();
        }

        try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            JsonElement root = GSON.fromJson(reader, JsonElement.class);
            return parseCatalog(resourceManager, root).sorted();
        } catch (IOException | JsonParseException exception) {
            ModpackAuthors.LOGGER.error("Failed to load author catalog {}; using empty catalog", CATALOG_LOCATION, exception);
            return AuthorCatalog.empty();
        }
    }

    private static AuthorCatalog parseCatalog(ResourceManager resourceManager, JsonElement root) {
        if (root == null || !root.isJsonObject()) {
            ModpackAuthors.LOGGER.error("Author catalog root must be a JSON object");
            return AuthorCatalog.empty();
        }

        JsonObject object = root.getAsJsonObject();
        int schemaVersion = getInt(object, "schemaVersion", -1);
        if (schemaVersion != 1) {
            ModpackAuthors.LOGGER.error("Unsupported author catalog schemaVersion {}; using empty catalog", schemaVersion);
            return AuthorCatalog.empty();
        }

        JsonArray authorArray = getArray(object, "authors");
        if (authorArray == null) {
            ModpackAuthors.LOGGER.error("Author catalog must contain an authors array; using empty catalog");
            return AuthorCatalog.empty();
        }

        String titleKey = getString(object, "titleKey", "screen.modpack_authors.title");
        Set<String> seenIds = new HashSet<>();
        List<AuthorProfile> authors = new ArrayList<>();

        for (int i = 0; i < authorArray.size(); i++) {
            Optional<AuthorProfile> author = parseAuthor(resourceManager, authorArray.get(i), i, seenIds);
            author.ifPresent(authors::add);
        }

        return new AuthorCatalog(schemaVersion, titleKey, authors);
    }

    private static Optional<AuthorProfile> parseAuthor(ResourceManager resourceManager, JsonElement element, int index, Set<String> seenIds) {
        if (element == null || !element.isJsonObject()) {
            ModpackAuthors.LOGGER.warn("Skipping author entry at index {}: entry must be an object", index);
            return Optional.empty();
        }

        JsonObject object = element.getAsJsonObject();
        String id = getString(object, "id", "").trim();
        LocalizedText displayName = getLocalizedText(object, "displayName", LocalizedText.of(""));

        if (id.isEmpty()) {
            ModpackAuthors.LOGGER.warn("Skipping author entry at index {}: id is required", index);
            return Optional.empty();
        }
        if (displayName.fallback().isBlank()) {
            ModpackAuthors.LOGGER.warn("Skipping author {}: displayName is required", id);
            return Optional.empty();
        }
        if (!seenIds.add(id)) {
            ModpackAuthors.LOGGER.warn("Skipping duplicate author id {}; keeping first entry", id);
            return Optional.empty();
        }

        LocalizedText shortDescription = getLocalizedText(object, "shortDescription", LocalizedText.of(""));
        LocalizedText longDescription = getLocalizedText(object, "longDescription", shortDescription);

        return Optional.of(new AuthorProfile(
                id,
                displayName,
                getLocalizedText(object, "role", LocalizedText.of("")),
                shortDescription,
                longDescription,
                resolveAvatar(resourceManager, getString(object, "avatar", "")),
                getInt(object, "order", 0),
                getLocalizedTextList(object, "tags"),
                getLocalizedTextList(object, "badges"),
                getLocalizedTextList(object, "contributions"),
                getStringList(object, "versions"),
                getLinks(object, id)
        ));
    }

    private static ResourceLocation resolveAvatar(ResourceManager resourceManager, String rawAvatar) {
        if (rawAvatar == null || rawAvatar.isBlank()) {
            return FALLBACK_AVATAR;
        }

        ResourceLocation location = rawAvatar.contains(":")
                ? ResourceLocation.tryParse(rawAvatar)
                : ResourceLocation.tryBuild(ModpackAuthors.MOD_ID, rawAvatar);

        if (location == null) {
            ModpackAuthors.LOGGER.warn("Avatar path {} is invalid; using fallback {}", rawAvatar, FALLBACK_AVATAR);
            return FALLBACK_AVATAR;
        }

        if (resourceManager.getResource(location).isPresent()) {
            return location;
        }

        ModpackAuthors.LOGGER.warn("Avatar {} is missing; using fallback {}", location, FALLBACK_AVATAR);
        return FALLBACK_AVATAR;
    }

    private static List<AuthorLink> getLinks(JsonObject object, String authorId) {
        JsonArray linksArray = getArray(object, "links");
        if (linksArray == null) {
            return List.of();
        }

        List<AuthorLink> links = new ArrayList<>();
        for (int i = 0; i < linksArray.size(); i++) {
            JsonElement element = linksArray.get(i);
            if (element == null || !element.isJsonObject()) {
                ModpackAuthors.LOGGER.warn("Skipping link {} for author {}: link must be an object", i, authorId);
                continue;
            }

            JsonObject linkObject = element.getAsJsonObject();
            String url = getString(linkObject, "url", "").trim();
            if (!isAllowedUrl(url)) {
                ModpackAuthors.LOGGER.warn("Skipping invalid URL for author {}: {}", authorId, url);
                continue;
            }

            String label = getString(linkObject, "label", "").trim();
            links.add(new AuthorLink(label.isEmpty() ? url : label, url));
        }

        return links;
    }

    private static boolean isAllowedUrl(String url) {
        if (url.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme))
                    && uri.getHost() != null
                    && !url.chars().anyMatch(Character::isISOControl);
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    private static String getString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        if (element == null || !element.isJsonPrimitive()) {
            return fallback;
        }

        try {
            return element.getAsString();
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        JsonElement element = object.get(key);
        if (element == null || !element.isJsonPrimitive()) {
            return fallback;
        }

        try {
            return element.getAsInt();
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private static JsonArray getArray(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    private static List<String> getStringList(JsonObject object, String key) {
        JsonArray array = getArray(object, key);
        if (array == null) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (JsonElement element : array) {
            if (element != null && element.isJsonPrimitive()) {
                String value = element.getAsString().trim();
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private static LocalizedText getLocalizedText(JsonObject object, String key, LocalizedText fallback) {
        JsonElement element = object.get(key);
        if (element == null) {
            return fallback;
        }
        return parseLocalizedText(element, fallback);
    }

    private static LocalizedText parseLocalizedText(JsonElement element, LocalizedText fallback) {
        if (element == null) {
            return fallback;
        }

        if (element.isJsonPrimitive()) {
            try {
                return LocalizedText.of(element.getAsString());
            } catch (RuntimeException exception) {
                return fallback;
            }
        }

        if (!element.isJsonObject()) {
            return fallback;
        }

        Map<String, String> translations = new LinkedHashMap<>();
        JsonObject object = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            JsonElement value = entry.getValue();
            if (value != null && value.isJsonPrimitive()) {
                String text = value.getAsString().trim();
                if (!text.isEmpty()) {
                    translations.put(entry.getKey(), text);
                }
            }
        }

        return translations.isEmpty() ? fallback : LocalizedText.of(translations);
    }

    private static List<LocalizedText> getLocalizedTextList(JsonObject object, String key) {
        JsonArray array = getArray(object, key);
        if (array == null) {
            return List.of();
        }

        List<LocalizedText> values = new ArrayList<>();
        for (JsonElement element : array) {
            LocalizedText value = parseLocalizedText(element, LocalizedText.of(""));
            if (!value.fallback().isBlank()) {
                values.add(value);
            }
        }
        return values;
    }
}
