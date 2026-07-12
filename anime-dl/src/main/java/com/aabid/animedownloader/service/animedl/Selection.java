package com.aabid.animedownloader.service.animedl;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.ServerInfo;

class Selection {

    private @NonNull final ServerInfo serverInfo;
    private @NonNull final Quality quality;

    public Selection(@NonNull ServerInfo serverInfo, @NonNull Quality quality) {
        this.serverInfo = serverInfo;
        this.quality = quality;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public Quality getQuality() {
        return quality;
    }

}
