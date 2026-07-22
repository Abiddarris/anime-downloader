package com.aabid.animedownloader.service.anilist;

import org.jetbrains.annotations.Nullable;

public class AnimeMetadata {

    private final int id;

    private final String romajiTitle;

    private final String nativeTitle;

    private final @Nullable String englishTitle;

    public AnimeMetadata(int id, String romajiTitle, String nativeTitle, String englishTitle) {
        this.id = id;
        this.romajiTitle = romajiTitle;
        this.nativeTitle = nativeTitle;
        this.englishTitle = englishTitle;
    }

    public int getId() {
        return id;
    }

    public String getRomajiTitle() {
        return romajiTitle;
    }

    public String getNativeTitle() {
        return nativeTitle;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

}
