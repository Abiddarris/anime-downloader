package com.aabid.animedownloader.source;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.ApiResponse.Provider;
import com.aabid.animedownloader.source.ApiResponse.StreamQuality;

class ApiResponseParser {

    @NonNull
    static Episode parseResponse(@NonNull ApiResponse response, @NonNull String link) {
        List<Server> servers = response.providers.stream()
            .map(ApiResponseParser::createServer)
            .toList();
        return new Episode(link, response.meta.anilist_id, response.meta.episode, servers);
    }

    @NonNull
    static Server createServer(@NonNull Provider provider) {
        List<Quality> qualities = createQualities(provider.qualities);
        return new Server(provider.id, provider.name, provider.status.equals("ready"), qualities);
    }

    @NonNull
    static List<Quality> createQualities(@NonNull List<StreamQuality> qualities) {
        return qualities.stream()
            .map(ApiResponseParser::createQuality)
            .toList();
    }

    @NonNull
    static Quality createQuality(StreamQuality quality) {
        if (quality.token == null && quality.fallbackToken == null) {
            return new DirectQuality(quality.name, quality.directUrl);
        }
        return new TokenBasedQuality(quality.name, quality.token, quality.fallbackToken);
    }
}
