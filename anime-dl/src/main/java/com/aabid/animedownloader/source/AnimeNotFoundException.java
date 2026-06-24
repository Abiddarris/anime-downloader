package com.aabid.animedownloader.source;

public class AnimeNotFoundException extends Exception {

    public AnimeNotFoundException(int anilistId) {
        super("Anime not found — AniList ID: " + anilistId);
    }
}
