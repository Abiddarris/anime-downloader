package com.aabid.animedownloader.source;

import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class Episode {

    private @NonNull List<Server> servers;
    private @NonNull Metadata metadata;

    public Episode(@NonNull Metadata metadata, @NonNull List<Server> servers) {
        this.metadata = metadata;
        this.servers = servers;
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

    public Metadata getMetadata() {
        return metadata;
    }

    @NonNull
    public String getSourceLink() {
        return metadata.getSource();
    }

    public int getId() {
        return metadata.getAnilistId();
    }

    public int getEpisode() {
        return metadata.getEpisode();
    }

}
