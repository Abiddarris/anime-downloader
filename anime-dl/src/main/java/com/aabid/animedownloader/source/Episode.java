package com.aabid.animedownloader.source;

import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class Episode {

    private @NonNull String sourceLink;
    private @NonNull List<Server> servers;
    private int id;
    private int episode;

    public Episode(@NonNull String sourceLink, int id, int episode, @NonNull List<Server> servers) {
        this.sourceLink = sourceLink;
        this.id = id;
        this.episode = episode;
        this.servers = servers;

        for (Server server : servers) {
            server.attach(this);
        }
    }

    public List<Server> getServers() {
        return Collections.unmodifiableList(servers);
    }

    public List<Server> getReadyServers() {
        return servers.stream()
            .filter(Server::isReady)
            .toList();
    }

    @Nullable
    public Server getReadyServer() {
        List<Server> servers = getReadyServers();
        return servers.isEmpty() ? null : servers.getFirst();
    }

    @Nullable
    public Server findServerById(String id) {
        return servers.stream()
            .filter(server -> server.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    @NonNull
    public String getSourceLink() {
        return sourceLink;
    }

    public int getId() {
        return id;
    }

    public int getEpisode() {
        return episode;
    }

}
