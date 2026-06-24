package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

public abstract class Quality {

    @NonNull
    private String name;

    @NonNull
    private Metadata metadata;

    public Quality(@NonNull String name, @NonNull Metadata metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @NonNull
    public Metadata getMetadata() {
        return metadata;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract String getLink();
    public abstract boolean isResolved();
}