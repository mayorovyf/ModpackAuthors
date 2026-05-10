package com.modpackauthors.client.icon;

import com.modpackauthors.ModpackAuthors;
import com.modpackauthors.data.AuthorLink;
import com.modpackauthors.util.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

public final class AuthorLinkIcons {
    public static final ResourceLocation DETAILS = icon("details");

    private static final ResourceLocation DEFAULT_LINK = icon("link");
    private static final List<KnownSite> KNOWN_SITES = List.of(
            new KnownSite("github", "GitHub", "github.com"),
            new KnownSite("gitlab", "GitLab", "gitlab.com"),
            new KnownSite("modrinth", "Modrinth", "modrinth.com"),
            new KnownSite("curseforge", "CurseForge", "curseforge.com"),
            new KnownSite("discord", "Discord", "discord.com", "discord.gg"),
            new KnownSite("youtube", "YouTube", "youtube.com", "youtu.be"),
            new KnownSite("twitch", "Twitch", "twitch.tv"),
            new KnownSite("x", "X / Twitter", "x.com", "twitter.com"),
            new KnownSite("telegram", "Telegram", "t.me", "telegram.me"),
            new KnownSite("vk", "VK", "vk.com"),
            new KnownSite("reddit", "Reddit", "reddit.com"),
            new KnownSite("artstation", "ArtStation", "artstation.com"),
            new KnownSite("behance", "Behance", "behance.net"),
            new KnownSite("soundcloud", "SoundCloud", "soundcloud.com"),
            new KnownSite("bandcamp", "Bandcamp", "bandcamp.com"),
            new KnownSite("patreon", "Patreon", "patreon.com"),
            new KnownSite("boosty", "Boosty", "boosty.to"),
            new KnownSite("kofi", "Ko-fi", "ko-fi.com"),
            new KnownSite("donationalerts", "DonationAlerts", "donationalerts.com")
    );

    private AuthorLinkIcons() {
    }

    public static AuthorLinkIcon iconFor(AuthorLink link) {
        String host = host(link.url());
        for (KnownSite site : KNOWN_SITES) {
            if (site.matches(host)) {
                return new AuthorLinkIcon(icon(site.iconName()), label(link, Components.literal(site.displayName())));
            }
        }

        return new AuthorLinkIcon(DEFAULT_LINK, label(link, host.isBlank()
                ? Components.translatable("screen.modpack_authors.open_link")
                : Components.literal(host)));
    }

    private static Component label(AuthorLink link, Component fallback) {
        return link.label().isBlank() ? fallback : Components.literal(link.label());
    }

    private static String host(String url) {
        try {
            String host = new URI(url).getHost();
            return host == null ? "" : host.toLowerCase(Locale.ROOT);
        } catch (URISyntaxException exception) {
            return "";
        }
    }

    private static ResourceLocation icon(String name) {
        return ModpackAuthors.id("textures/gui/icons/" + name + ".png");
    }

    private record KnownSite(String iconName, String displayName, List<String> domains) {
        private KnownSite(String iconName, String displayName, String... domains) {
            this(iconName, displayName, List.of(domains));
        }

        private boolean matches(String host) {
            for (String domain : this.domains) {
                if (host.equals(domain) || host.endsWith("." + domain)) {
                    return true;
                }
            }
            return false;
        }
    }
}
