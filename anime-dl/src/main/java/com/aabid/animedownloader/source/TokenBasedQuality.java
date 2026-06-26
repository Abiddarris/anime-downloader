package com.aabid.animedownloader.source;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class TokenBasedQuality extends Quality {

    @NonNull
    private final String token;

    @NonNull
    private final String fallbackToken;

    @Nullable
    private String link;

    @NonNull
    private EpisodeContext context;

    public TokenBasedQuality(@NonNull String name, @NonNull Metadata metadata,
                             @NonNull EpisodeContext context, @NonNull String token, @NonNull String fallbackToken) {
        super(name, metadata);

        this.token = token;
        this.context = context;
        this.fallbackToken = fallbackToken;
    }

    EpisodeContext getContext() {
        return context;
    }

    @Override
    public boolean isResolved() {
        return link != null;
    }

    @Override
    @Nullable
    public String getLink() {
        return link;
    }

    public void resolve(@NonNull String link) {
        if (isResolved()) {
            throw new IllegalStateException("Quality has been resolved");
        }

        Objects.requireNonNull(link, "link must not be null");

        this.link = link;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    @NonNull
    public String getFallbackToken() {
        return fallbackToken;
    }

}
