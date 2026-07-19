/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aabid.animedownloader.service.tryembed;

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
