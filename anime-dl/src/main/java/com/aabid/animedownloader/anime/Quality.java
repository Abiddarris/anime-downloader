package com.aabid.animedownloader.anime;

import org.jspecify.annotations.NonNull;

public abstract class Quality {

    @NonNull
    private String name;

    public Quality(@NonNull String name) {
        this.name = name;
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