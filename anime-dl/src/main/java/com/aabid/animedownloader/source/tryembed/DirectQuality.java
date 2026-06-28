package com.aabid.animedownloader.source.tryembed;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.source.Quality;

class DirectQuality extends Quality {

    @NonNull
    private String link;

    public DirectQuality(@NonNull String name, @NonNull String link) {
        super(name);

        this.link = link;
    }

    public String getLink() {
        return link;
    }

}
