package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

public class ServerInfo {

    @NonNull
    private String id;

    @NonNull
    private String name;

    public ServerInfo(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
