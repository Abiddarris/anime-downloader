package com.aabid.animedownloader.service.animedl;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;

public interface StreamSelector {

    @Nullable
    Selection select(@NonNull Episode episode) throws IOException, AnimeServiceException;
}
