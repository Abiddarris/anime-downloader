package com.aabid.animedownloader.source;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class Episode {

    public abstract String resolveQuality(Quality quality) throws IOException;

    public abstract EpisodeInfo getEpisodeInfo();

    public abstract List<ServerInfo> getServers();

    public abstract Server fetchServer(ServerInfo info) throws IOException;

    public final Optional<ServerInfo> findServerById(String id) {
        return getServers().stream()
                .filter(server -> server.getId().equals(id))
                .findFirst();
    }
}