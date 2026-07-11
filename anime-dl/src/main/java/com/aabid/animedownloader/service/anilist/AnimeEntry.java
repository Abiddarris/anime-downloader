package com.aabid.animedownloader.service.anilist;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AnimeEntry {

    private int id;

    @NonNull
    private final String romajiTitle;

    @NonNull
    private final String englishTitle;

    @NonNull
    private final String format;

    @Nullable
    private final Integer episodeCount;

    public AnimeEntry(int id, @NonNull String romajiTitle, @NonNull String englishTitle, @NonNull String format,
            @Nullable Integer episodeCount) {
        this.id = id;
        this.romajiTitle = romajiTitle;
        this.englishTitle = englishTitle;
        this.format = format;
        this.episodeCount = episodeCount;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getRomajiTitle() {
        return romajiTitle;
    }

    @NonNull
    public String getEnglishTitle() {
        return englishTitle;
    }

    @NonNull
    public String getFormat() {
        return format;
    }

    @Nullable
    public Integer getEpisodeCount() {
        return episodeCount;
    }

}
