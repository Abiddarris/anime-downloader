package com.aabid.animedownloader.source;

public class Quality {

    private String name;
    private String token;
    private String fallbackToken;

    public Quality(String name, String token, String fallbackToken) {
        this.name = name;
        this.token = token;
        this.fallbackToken = fallbackToken;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getFallbackToken() {
        return fallbackToken;
    }

}
