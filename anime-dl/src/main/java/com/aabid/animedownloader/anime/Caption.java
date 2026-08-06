package com.aabid.animedownloader.anime;

import org.jspecify.annotations.NonNull;

public class Caption {

    private final @NonNull String name;
    private final @NonNull String id;
    private final @NonNull String url;
    private final @NonNull String format;

    public Caption(@NonNull String name, @NonNull String id, @NonNull String url, @NonNull String format) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.format = format;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getFormat() {
        return format;
    }

}
