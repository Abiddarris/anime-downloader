package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

public class Metadata {

    private final int anilistId;
    private final int episode;

    @NonNull
    private final String title;

    public Metadata(int anilistId, int episode, @NonNull String title) {
        this.anilistId = anilistId;
        this.episode = episode;
        this.title = title;
    }

    public int getAnilistId() {
        return anilistId;
    }

    public int getEpisode() {
        return episode;
    }

    @NonNull
    public String getAnimeTitle() {
        return title;
    }
}
