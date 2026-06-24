package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

public class DirectQuality extends Quality {

    @NonNull
    private String link;

    public DirectQuality(@NonNull String name, @NonNull Metadata metadata, @NonNull String link) {
        super(name, metadata);

        this.link = link;
    }

    @Override
    @NonNull
    public String getLink() {
        return link;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

}
