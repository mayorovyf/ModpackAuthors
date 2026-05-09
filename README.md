# Modpack Authors

This is a Forge mod that adds a separate modpack authors screen to the Minecraft main menu. Players can use it to open the team list, find a specific member, and view their role, description, contacts, project contribution, and the versions they worked on.

The mod does not add server logic and does not change gameplay. It works only on the client and reads author data from the pack config directory first, then falls back to bundled resources. This is useful for modpacks because the team list can be updated through JSON files without rebuilding the mod.

The `Authors` button is added directly to the main menu. The authors screen supports search by name, role, description, tags, contribution, and versions. Each author row shows an avatar, name, role, short description, quick links, and a profile details button.

## How To Use

The main editable file with the author list is stored in the Minecraft config directory:

```text
config/modpack_authors/authors.json
```

The same author format can also be split into one file per author:

```text
config/modpack_authors/authors/lexeef.json
config/modpack_authors/authors/ender.json
```

If at least one `config/modpack_authors/authors/*.json` file exists, the mod loads authors from that folder. If the folder is empty or missing, it loads `config/modpack_authors/authors.json`. If no config file exists, the mod creates a default `authors.json` from its bundled fallback and loads that.

Avatars should be placed in this folder:

```text
assets/modpack_authors/textures/gui/authors/
```

The avatar path in `authors.json` is relative to `assets/modpack_authors/`. For example, if the file is located at `assets/modpack_authors/textures/gui/authors/mayo.png`, the author profile should use:

```json
"avatar": "textures/gui/authors/mayo.png"
```

If an avatar is not set or the file is missing, the mod uses the default `default.png` fallback.

Links open only through Minecraft's standard confirmation screen. The mod accepts only `https://` and `http://`. Links with other schemes are ignored so the player cannot accidentally open an unsafe path.

## Author Example

A minimal profile can look like this:

```json
{
  "id": "mayo",
  "displayName": "Mayo",
  "role": {
    "en_us": "Modpack Author",
    "ru_ru": "Автор модпака"
  },
  "shortDescription": {
    "en_us": "Quests, balance and progression.",
    "ru_ru": "Квесты, баланс и прогрессия."
  },
  "longDescription": {
    "en_us": "Worked on quest flow, progression pacing, mod integration and final pack polish.",
    "ru_ru": "Работал над квестами, темпом прогрессии, интеграцией модов и финальной полировкой сборки."
  },
  "avatar": "textures/gui/authors/mayo.png",
  "order": 10,
  "tags": ["quests", "balance", "progression"],
  "badges": [
    {
      "en_us": "Core Team",
      "ru_ru": "Основная команда"
    }
  ],
  "contributions": [
    {
      "en_us": "Quest book",
      "ru_ru": "Книга квестов"
    },
    {
      "en_us": "Progression balance",
      "ru_ru": "Баланс прогрессии"
    },
    {
      "en_us": "Mod integration",
      "ru_ru": "Интеграция модов"
    }
  ],
  "versions": [
    "0.2.0"
  ],
  "links": [
    {
      "label": "GitHub",
      "url": "https://github.com/username"
    },
    {
      "label": "Modrinth",
      "url": "https://modrinth.com/user/username"
    }
  ]
}
```

The `id` field must be unique. If two authors use the same `id`, the mod keeps the first one and skips the rest with a warning in the log.

Text fields can be plain strings or localized objects. When a localized object is used, the mod chooses the value that matches the language selected in Minecraft settings.

```json
"shortDescription": {
  "en_us": "Quests, balance and progression.",
  "ru_ru": "Квесты, баланс и прогрессия."
}
```

If the current language is missing, the mod falls back to `default`, then `en_us`, then the first available value.

The `order` field controls sorting. The lower the number, the higher the author appears in the list. If the order is the same, authors are sorted by `displayName`.

The `tags`, `badges`, `contributions`, `versions`, and `links` fields can be empty arrays. The screen simply does not show empty sections.

## Full authors.json File

The root file must contain the schema version, title key, and author array:

```json
{
  "schemaVersion": 1,
  "titleKey": "screen.modpack_authors.title",
  "authors": [
    {
      "id": "mayo",
      "displayName": "Mayo",
      "role": {
        "en_us": "Modpack Author",
        "ru_ru": "Автор модпака"
      },
      "shortDescription": {
        "en_us": "Quests, balance and progression.",
        "ru_ru": "Квесты, баланс и прогрессия."
      },
      "longDescription": {
        "en_us": "Worked on quest flow, progression pacing, mod integration and final pack polish.",
        "ru_ru": "Работал над квестами, темпом прогрессии, интеграцией модов и финальной полировкой сборки."
      },
      "avatar": "textures/gui/authors/mayo.png",
      "order": 10,
      "tags": ["quests", "balance"],
      "badges": ["Core Team"],
      "contributions": ["Quest book", "Progression balance"],
      "versions": ["0.2.0"],
      "links": []
    }
  ]
}
```

If the config files are missing, the mod creates a default `config/modpack_authors/authors.json` and falls back to bundled data. If the root JSON is broken, the mod loads an empty catalog and writes an error to the log. If only one author entry is broken, the mod skips that author and continues loading the rest.

## Per-Author Files

When separate author files are used, each file contains a single author object instead of the root `authors` array:

```json
{
  "id": "mayo",
  "displayName": "Mayo",
  "role": "Modpack Author",
  "shortDescription": "Quests, balance and progression.",
  "longDescription": "Worked on quest flow, progression pacing, mod integration and final pack polish.",
  "avatar": "textures/gui/authors/mayo.png",
  "order": 10,
  "tags": ["quests", "balance", "progression"],
  "badges": ["Core Team"],
  "contacts": ["Discord: mayo"],
  "contributions": ["Quest book", "Progression balance"],
  "versions": ["0.2.0"],
  "links": [
    {
      "label": "GitHub",
      "url": "https://github.com/username"
    }
  ]
}
```

The optional root file `config/modpack_authors/authors.json` can still be kept beside the folder to provide `titleKey`; when per-author files exist, its `authors` array is ignored.

## Links And Icons

For popular sites, the mod automatically shows monochrome PNG icons. GitHub, GitLab, Modrinth, CurseForge, Discord, YouTube, Twitch, X / Twitter, Telegram, VK, Reddit, ArtStation, Behance, SoundCloud, Bandcamp, Patreon, Boosty, Ko-fi, and DonationAlerts are currently supported.

The icon is selected by the link domain. For example, `https://github.com/name` gets the GitHub icon, and `https://modrinth.com/user/name` gets the Modrinth icon. If the site is unknown, the mod shows a generic link icon.

Icon files are stored here:

```text
assets/modpack_authors/textures/gui/icons/
```

You can replace the icons with your own PNG files using the same file names. No code changes are needed.

## Main Menu

By default, the `Authors` button is inserted below the multiplayer button in the Minecraft main menu. With the default `BELOW_MULTIPLAYER` anchor, the mod also keeps the main title buttons in a responsive centered stack, so button width and row positions are recalculated from the current screen size instead of relying on fixed FancyMenu coordinates.

The button position can be changed in the client config:

```text
config/modpack_authors-client.toml
```

Main settings:

```toml
[mainMenuButton]
showMainMenuButton = true
buttonAnchor = "BELOW_MULTIPLAYER"
offsetX = 0
offsetY = 0
buttonWidth = 200
buttonHeight = 20
```

If the button is not needed, set `showMainMenuButton = false`.

## Localization

All interface strings are stored in lang files:

```text
assets/modpack_authors/lang/en_us.json
assets/modpack_authors/lang/ru_ru.json
```

Author names, roles, and descriptions are stored in `config/modpack_authors/authors.json` or separate files under `config/modpack_authors/authors/`. Text fields can be plain strings or localized objects, so the same config can carry both English and Russian profile text.
