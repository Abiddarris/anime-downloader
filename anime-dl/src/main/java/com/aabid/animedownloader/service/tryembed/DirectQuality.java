package com.aabid.animedownloader.service.tryembed;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.Quality;

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
