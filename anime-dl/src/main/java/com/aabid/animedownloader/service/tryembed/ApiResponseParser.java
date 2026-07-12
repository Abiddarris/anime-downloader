package com.aabid.animedownloader.service.tryembed;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.aabid.animedownloader.anime.AnimeNotFoundException;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerException;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.service.tryembed.ApiResponse.Provider;
import com.aabid.animedownloader.service.tryembed.ApiResponse.StreamQuality;

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

    @Nullable
    static ServerInfo getReadyServer(List<ServerInfo> servers, ApiResponse response) {
        if (response.selectedProvider == null) {
            return null;
        }

        return servers.stream()
            .filter(server -> response.selectedProvider.id.equals(server.getId()))
            .findFirst()
            .orElse(null);
    }

}
