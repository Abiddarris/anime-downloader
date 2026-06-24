package com.aabid.animedownloader.source;

import org.jspecify.annotations.NonNull;

public class Metadata {

    private final int anilistId;
    private final int episode;

    @NonNull
    private final String title;

    @NonNull
    private final String source;

    public Metadata(int anilistId, int episode, @NonNull String title, @NonNull String source) {
        this.anilistId = anilistId;
        this.episode = episode;
        this.title = title;
        this.source = source;
    }

    @NonNull
    public String getSource() {
        return source;
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
