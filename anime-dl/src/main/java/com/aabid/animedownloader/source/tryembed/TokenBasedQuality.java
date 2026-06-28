package com.aabid.animedownloader.source.tryembed;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.Quality;

class TokenBasedQuality extends Quality {

    @NonNull
    private final String token;

    @NonNull
    private final String fallbackToken;

    public TokenBasedQuality(@NonNull String name, @NonNull String token, @NonNull String fallbackToken) {
        super(name);

        this.token = token;
        this.fallbackToken = fallbackToken;
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
