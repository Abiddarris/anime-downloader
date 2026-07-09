package com.aabid.animedownloader.source.tryembed;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.AnimeNotFoundException;
import com.aabid.animedownloader.source.EpisodeInfo;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;
import com.aabid.animedownloader.source.ServerException;
import com.aabid.animedownloader.source.ServerInfo;
import com.aabid.animedownloader.source.tryembed.ApiResponse.Provider;
import com.aabid.animedownloader.source.tryembed.ApiResponse.StreamQuality;

class ApiResponseParser {

    @NonNull
    static EpisodeInfo createEpisodeInfo(@NonNull ApiResponse response) throws AnimeNotFoundException {
        checkIfAnimeReallyExists(response);
        return new EpisodeInfo(response.meta.anilist_id, response.meta.episode, response.animeTitle);
    }

    private static void checkIfAnimeReallyExists(@NonNull ApiResponse response) throws AnimeNotFoundException {
        if (response.animeTitle != null || response.selectedProvider != null) {
            return;
        }

        if (response.outro == null && response.intro == null && response.posterUrl == null) {
            throw new AnimeNotFoundException(response.meta.anilist_id);
        }
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
    static Server createServer(@NonNull ServerInfo server, @NonNull Provider provider)
            throws IOException, ServerException {
        return switch (provider.status) {
            case FAILED -> {
                throw new ServerException("Server '" + server.getId() + "' returned a failure status");
            }
            case IDLE -> {
                throw new ServerException("Server '" + server.getId() + "' is not ready (Unexpected IDLE status)");
            }
            case READY -> new Server(server, createQualities(provider.qualities));
        };
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
