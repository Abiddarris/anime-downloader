package com.aabid.animedownloader.source;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.ApiResponse.Provider;
import com.aabid.animedownloader.source.ApiResponse.StreamQuality;

class ApiResponseParser {

    @NonNull
    static EpisodeInfo createEpisodeInfo(@NonNull ApiResponse response) throws AnimeNotFoundException {
        if (response.animeTitle == null) {
            throw new AnimeNotFoundException(response.meta.anilist_id);
        }

        return new EpisodeInfo(response.meta.anilist_id, response.meta.episode, response.animeTitle);
    }

    @NonNull
    static List<ServerInfo> createServers(List<Provider> providers) {
        return providers.stream()
            .map(ApiResponseParser::createServer)
            .toList();
    }

    @NonNull
    private static ServerInfo createServer(@NonNull Provider provider) {
        return new ServerInfo(provider.id, provider.name);
    }

    @NonNull
    static Server createServer(@NonNull ServerInfo server, @NonNull Provider provider) throws IOException {
         switch (provider.status) {
            case FAILED -> {
                throw new IOException("Fail to fetch server");
            }
            case READY -> {

            }
            case IDLE -> {
                throw new AssertionError();
            }
        };
        return new Server(server, createQualities(provider.qualities));
    }

    @NonNull
    private static List<Quality> createQualities(@NonNull List<StreamQuality> qualities) {
        return qualities.stream()
                .map(ApiResponseParser::createQuality)
                .toList();
    }

    @NonNull
    private static Quality createQuality(@NonNull StreamQuality quality) {
        String name = getStandarizedName(quality);
        if (quality.token == null && quality.fallbackToken == null) {
            return new DirectQuality(name, quality.directUrl);
        }
        return new TokenBasedQuality(name, quality.token, quality.fallbackToken);
    }

    @NonNull
    private static String getStandarizedName(@NonNull StreamQuality quality) {
        if (quality.name.equals("Default")) {
            return quality.name;
        }

        if (quality.name.endsWith("p")) {
            return quality.name;
        }

        int end = quality.name.indexOf("p");
        if (end == -1) {
            throw new IllegalStateException("Unknown resolution name");
        }

        return quality.name.substring(0, end + 1);
    }

}
