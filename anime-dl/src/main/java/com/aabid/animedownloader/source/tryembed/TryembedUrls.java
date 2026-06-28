package com.aabid.animedownloader.source.tryembed;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import okhttp3.HttpUrl;

class TryembedUrls {

    private static final String SCHEME = "https";
    private static final String HOST = "tryembed.us.cc";

    static HttpUrl getEpisodeUrl(int animeId, int episode) {
        return new HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegments("embed/anime")
            .addPathSegment(String.valueOf(animeId))
            .addPathSegment(String.valueOf(episode))
            .addPathSegment("sub")
            .build();
    }

    @NonNull
    static HttpUrl getTokenResolutionUrl(String token) {
        return new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment("s")
                .addPathSegment(token + ".m3u8")
                .build();
    }

    @NonNull
    static HttpUrl getEpisodeApiUrl(int animeId, int episodeNumber, @Nullable String server, @NonNull String nonce) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment("api")
                .addPathSegment("stream_data")
                .addQueryParameter("id", String.valueOf(animeId))
                .addQueryParameter("episode", String.valueOf(episodeNumber))
                .addQueryParameter("audio", "sub");

        if (server != null) {
            builder.addQueryParameter("server", server);
        }

        builder.addQueryParameter("nonce", nonce);

        return builder.build();
    }

}
