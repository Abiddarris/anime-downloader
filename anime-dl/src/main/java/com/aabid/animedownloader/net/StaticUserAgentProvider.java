package com.aabid.animedownloader.net;

import org.jspecify.annotations.NonNull;

public class StaticUserAgentProvider implements UserAgentProvider {

    private @NonNull String userAgent;

    public StaticUserAgentProvider(@NonNull String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

}
