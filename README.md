# Modpack Authors

This is a Forge mod that adds a separate modpack authors screen to the Minecraft main menu. Players can use it to open the team list, find a specific member, and view their role, description, contacts, project contribution, and the versions they worked on.

The mod does not add server logic and does not change gameplay. It works only on the client and reads author data from resources. This is useful for modpacks because the team list can be updated through `authors.json` and avatars, without changing Java code.

The `Authors` button is added directly to the main menu. The authors screen supports search by name, role, description, tags, contribution, and versions. Each author row shows an avatar, name, role, short description, quick links, and a profile details button.

## How To Use

The main file with the author list is stored in resources:

```text
assets/modpack_authors/authors/authors.json
```

The example in the source tree is here:

```text
src/main/resources/assets/modpack_authors/authors/authors.json
```

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
  "role": "Modpack Author",
  "shortDescription": "Quests, balance and progression.",
  "longDescription": "Worked on quest flow, progression pacing, mod integration and final pack polish.",
  "avatar": "textures/gui/authors/mayo.png",
  "order": 10,
  "tags": ["quests", "balance", "progression"],
  "badges": ["Core Team"],
  "contributions": [
    "Quest book",
    "Progression balance",
    "Mod integration"
  ],
  "versions": [
    "0.1.0"
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
      "role": "Modpack Author",
      "shortDescription": "Quests, balance and progression.",
      "longDescription": "Worked on quest flow, progression pacing, mod integration and final pack polish.",
      "avatar": "textures/gui/authors/mayo.png",
      "order": 10,
      "tags": ["quests", "balance"],
      "badges": ["Core Team"],
      "contributions": ["Quest book", "Progression balance"],
      "versions": ["0.1.0"],
      "links": []
    }
  ]
}
```

If `authors.json` is missing, the screen still opens and shows an empty state. If the root JSON is broken, the mod loads an empty catalog and writes an error to the log. If only one author entry is broken, the mod skips that author and continues loading the rest.

## Links And Icons

For popular sites, the mod automatically shows monochrome PNG icons. GitHub, GitLab, Modrinth, CurseForge, Discord, YouTube, Twitch, X / Twitter, Telegram, VK, Reddit, ArtStation, Behance, SoundCloud, Bandcamp, Patreon, Boosty, Ko-fi, and DonationAlerts are currently supported.

The icon is selected by the link domain. For example, `https://github.com/name` gets the GitHub icon, and `https://modrinth.com/user/name` gets the Modrinth icon. If the site is unknown, the mod shows a generic link icon.

Icon files are stored here:

```text
assets/modpack_authors/textures/gui/icons/
```

You can replace the icons with your own PNG files using the same file names. No code changes are needed.

## Main Menu

By default, the `Authors` button is inserted below the multiplayer button in the Minecraft main menu. It uses the width of a standard menu button and moves the lower buttons down so it does not overlap `Mods`, `Realms`, `Options`, or `Quit`.

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

Author names, roles, and descriptions are currently stored directly in `authors.json`. This is simpler for modpacks where the team list is usually written in one language. If full localization for profiles is needed, different versions of `authors.json` can be provided through a resource pack.
