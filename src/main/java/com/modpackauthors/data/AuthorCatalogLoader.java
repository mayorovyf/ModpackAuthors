package com.modpackauthors.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.modpackauthors.ModpackAuthors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class AuthorCatalogLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ResourceLocation CATALOG_LOCATION = ModpackAuthors.id("authors/authors.json");
    private static final ResourceLocation FALLBACK_AVATAR = ModpackAuthors.id("textures/gui/authors/default.png");
    private static final String DEFAULT_TITLE_KEY = "screen.modpack_authors.title";
    private static final String CONFIG_CATALOG_FILE = "authors.json";
    private static final String CONFIG_AUTHOR_DIRECTORY = "authors";

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
        Path configDirectory = configDirectory();
        Path configCatalogPath = configDirectory.resolve(CONFIG_CATALOG_FILE);
        Path configAuthorsDirectory = configDirectory.resolve(CONFIG_AUTHOR_DIRECTORY);
        List<Path> authorFiles = getAuthorFiles(configAuthorsDirectory);

        if (!authorFiles.isEmpty()) {
            return loadIndividualAuthorFiles(resourceManager, configCatalogPath, authorFiles).sorted();
        }

        if (Files.isRegularFile(configCatalogPath)) {
            return loadConfigCatalog(resourceManager, configCatalogPath).sorted();
        }

        ensureDefaultConfig(resourceManager, configCatalogPath);
        return loadResourceCatalog(resourceManager).sorted();
    }

    private static AuthorCatalog loadResourceCatalog(ResourceManager resourceManager) {
        Optional<Resource> resource = resourceManager.getResource(CATALOG_LOCATION);
        if (resource.isEmpty()) {
            ModpackAuthors.LOGGER.warn("Author catalog {} is missing; using empty catalog", CATALOG_LOCATION);
            return AuthorCatalog.empty();
        }

        try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            JsonElement root = GSON.fromJson(reader, JsonElement.class);
            return parseCatalog(resourceManager, root, CATALOG_LOCATION.toString());
        } catch (IOException | JsonParseException exception) {
            ModpackAuthors.LOGGER.error("Failed to load author catalog {}; using empty catalog", CATALOG_LOCATION, exception);
            return AuthorCatalog.empty();
        }
    }

    private static AuthorCatalog loadConfigCatalog(ResourceManager resourceManager, Path catalogPath) {
        try (Reader reader = Files.newBufferedReader(catalogPath, StandardCharsets.UTF_8)) {
            JsonElement root = GSON.fromJson(reader, JsonElement.class);
            return parseCatalog(resourceManager, root, catalogPath.toString());
        } catch (IOException | JsonParseException exception) {
            ModpackAuthors.LOGGER.error("Failed to load author catalog config {}; using empty catalog", catalogPath, exception);
            return AuthorCatalog.empty();
        }
    }

    private static AuthorCatalog loadIndividualAuthorFiles(ResourceManager resourceManager, Path catalogPath, List<Path> authorFiles) {
        String titleKey = readTitleKey(catalogPath).orElse(DEFAULT_TITLE_KEY);
        Set<String> seenIds = new HashSet<>();
        List<AuthorProfile> authors = new ArrayList<>();

        for (Path authorFile : authorFiles) {
            try (Reader reader = Files.newBufferedReader(authorFile, StandardCharsets.UTF_8)) {
                JsonElement root = GSON.fromJson(reader, JsonElement.class);
                Optional<AuthorProfile> author = parseAuthor(resourceManager, root, authorFile.toString(), seenIds);
                author.ifPresent(authors::add);
            } catch (IOException | JsonParseException exception) {
                ModpackAuthors.LOGGER.error("Failed to load author config {}; skipping it", authorFile, exception);
            }
        }

        return new AuthorCatalog(1, titleKey, authors);
    }

    private static Optional<String> readTitleKey(Path catalogPath) {
        if (!Files.isRegularFile(catalogPath)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(catalogPath, StandardCharsets.UTF_8)) {
            JsonElement root = GSON.fromJson(reader, JsonElement.class);
            if (root == null || !root.isJsonObject()) {
                return Optional.empty();
            }

            String titleKey = getString(root.getAsJsonObject(), "titleKey", "").trim();
            return titleKey.isEmpty() ? Optional.empty() : Optional.of(titleKey);
        } catch (IOException | JsonParseException exception) {
            ModpackAuthors.LOGGER.warn("Failed to read author catalog title key from {}", catalogPath, exception);
            return Optional.empty();
        }
    }

    private static List<Path> getAuthorFiles(Path directory) {
        if (!Files.isDirectory(directory)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (IOException exception) {
            ModpackAuthors.LOGGER.error("Failed to list author config directory {}; using fallback catalog", directory, exception);
            return List.of();
        }
    }

    private static void ensureDefaultConfig(ResourceManager resourceManager, Path catalogPath) {
        if (Files.exists(catalogPath)) {
            return;
        }

        Optional<Resource> resource = resourceManager.getResource(CATALOG_LOCATION);
        if (resource.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(catalogPath.getParent());
            JsonElement root;
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                root = GSON.fromJson(reader, JsonElement.class);
            }

            try (Writer writer = Files.newBufferedWriter(catalogPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
                GSON.toJson(root, writer);
            }
            ModpackAuthors.LOGGER.info("Created default author catalog config at {}", catalogPath);
        } catch (FileAlreadyExistsException ignored) {
            // Another reload created the file first.
        } catch (IOException | JsonParseException exception) {
            ModpackAuthors.LOGGER.warn("Failed to create default author catalog config at {}", catalogPath, exception);
        }
    }

    private static Path configDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(ModpackAuthors.MOD_ID);
    }

    private static AuthorCatalog parseCatalog(ResourceManager resourceManager, JsonElement root, String source) {
        if (root == null || !root.isJsonObject()) {
            ModpackAuthors.LOGGER.error("Author catalog {} root must be a JSON object", source);
            return AuthorCatalog.empty();
        }

        JsonObject object = root.getAsJsonObject();
        int schemaVersion = getInt(object, "schemaVersion", -1);
        if (schemaVersion != 1) {
            ModpackAuthors.LOGGER.error("Unsupported author catalog schemaVersion {} in {}; using empty catalog", schemaVersion, source);
            return AuthorCatalog.empty();
        }

        JsonArray authorArray = getArray(object, "authors");
        if (authorArray == null) {
            ModpackAuthors.LOGGER.error("Author catalog {} must contain an authors array; using empty catalog", source);
            return AuthorCatalog.empty();
        }

        String titleKey = getString(object, "titleKey", DEFAULT_TITLE_KEY);
        Set<String> seenIds = new HashSet<>();
        List<AuthorProfile> authors = new ArrayList<>();

        for (int i = 0; i < authorArray.size(); i++) {
            Optional<AuthorProfile> author = parseAuthor(resourceManager, authorArray.get(i), source + " authors[" + i + "]", seenIds);
            author.ifPresent(authors::add);
        }

        return new AuthorCatalog(schemaVersion, titleKey, authors);
    }

    private static Optional<AuthorProfile> parseAuthor(ResourceManager resourceManager, JsonElement element, String source, Set<String> seenIds) {
        if (element == null || !element.isJsonObject()) {
            ModpackAuthors.LOGGER.warn("Skipping author entry {}: entry must be an object", source);
            return Optional.empty();
        }

        JsonObject object = element.getAsJsonObject();
        String id = getString(object, "id", "").trim();
        LocalizedText displayName = getLocalizedText(object, "displayName", LocalizedText.of(""));

        if (id.isEmpty()) {
            ModpackAuthors.LOGGER.warn("Skipping author entry {}: id is required", source);
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
                getLocalizedTextList(object, "contacts"),
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
