package com.aabid.animedownloader.source;

import java.util.Objects;

public class TokenBasedQuality extends Quality {

    private final String token;
    private final String fallbackToken;

    private String link;

    public TokenBasedQuality(String name, String token, String fallbackToken) {
        super(name);

        this.token = token;
        this.fallbackToken = fallbackToken;
    }

    @Override
    public boolean isResolved() {
        return link != null;
    }

    @Override
    public String getLink() {
        return link;
    }

    public void resolve(String link) {
        if (isResolved()) {
            throw new IllegalStateException("Quality has been resolved");
        }

        Objects.requireNonNull(link, "link must not be null");

        this.link = link;
    }

    public String getToken() {
        return token;
    }

    public String getFallbackToken() {
        return fallbackToken;
    }

}
