package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

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
