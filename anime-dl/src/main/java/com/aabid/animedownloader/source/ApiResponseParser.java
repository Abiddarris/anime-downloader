package com.aabid.animedownloader.source;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.ApiResponse.Provider;
import com.aabid.animedownloader.source.ApiResponse.Status;
import com.aabid.animedownloader.source.ApiResponse.StreamQuality;
import com.aabid.animedownloader.source.Server.ServerState;

class ApiResponseParser {

    @NonNull
    static Episode createEpisode(@NonNull ApiResponse response, @NonNull String link) throws AnimeNotFoundException {
        if (response.animeTitle == null) {
            throw new AnimeNotFoundException(response.meta.anilist_id);
        }

        Metadata metadata = new Metadata(response.meta.anilist_id, response.meta.episode, response.animeTitle, link);
        List<@NonNull Server> servers = response.providers.stream()
            .map(provider -> createServer(provider, metadata))
            .toList();
        return new Episode(metadata, servers);
    }

    @NonNull
    static Server createServer(@NonNull Provider provider, @NonNull Metadata metadata) {
        List<Quality> qualities = createQualities(provider.qualities, metadata);
        return new Server(metadata, provider.id, provider.name, translateStatus(provider.status), qualities);
    }

    private static ServerState translateStatus(Status status) {
        return switch (status) {
            case FAILED -> ServerState.FAILED;
            case IDLE -> ServerState.IDLE;
            case READY -> ServerState.READY;
        };
    }

    @NonNull
    static List<Quality> createQualities(@NonNull List<StreamQuality> qualities, @NonNull Metadata metadata) {
        return qualities.stream()
            .map(quality -> createQuality(quality, metadata))
            .toList();
    }

    @NonNull
    static Quality createQuality(@NonNull StreamQuality quality, @NonNull Metadata metadata) {
        String name = getStandarizedName(quality);
        if (quality.token == null && quality.fallbackToken == null) {
            return new DirectQuality(name, metadata, quality.directUrl);
        }
        return new TokenBasedQuality(name, metadata, quality.token, quality.fallbackToken);
    }

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

    static void updateServerStatusFromResponse(Server server, ApiResponse response) {
        Provider provider = response.providers.stream()
                .filter(p -> p.id.equals(server.getId()))
                .findFirst()
                .get();

        server.resolve(createQualities(provider.qualities, server.getMetadata()), translateStatus(provider.status));
    }
}
