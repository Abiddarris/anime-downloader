package com.aabid.animedownloader.source;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

public abstract class Episode {

    @NonNull
    public abstract EpisodeInfo getEpisodeInfo();

    @NonNull
    public abstract List<ServerInfo> getServers();

    @NonNull
    public abstract Server fetchServer(ServerInfo info) throws IOException, ServerException;

    @NonNull
    public abstract String resolveQuality(Quality quality) throws IOException;

    public final Optional<ServerInfo> findServerById(String id) {
        return getServers().stream()
                .filter(server -> server.getId().equals(id))
                .findFirst();
    }
}