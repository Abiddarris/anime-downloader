package com.aabid.animedownloader.anime;

import java.io.IOException;

public interface AnimeService {
    Episode queryEpisode(int animeId, int episode) throws IOException, AnimeServiceException;
}