package com.aabid.animedownloader.source;

import java.io.IOException;

public interface AnimeService {

    Episode queryAnime(int animeId, int episode) throws IOException, AnimeNotFoundException;

    void fetchServer(Server server) throws IOException;

    void resolveQuality(Quality quality) throws IOException;

}