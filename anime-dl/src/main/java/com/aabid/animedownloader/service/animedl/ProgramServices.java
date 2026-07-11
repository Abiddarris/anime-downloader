package com.aabid.animedownloader.service.animedl;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anilist.AnilistService;
import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.service.ytdlp.YtDlpService;

public class ProgramServices {

    @NonNull
    private AnimeService source;

    @NonNull
    private YtDlpService ytDlpService;

    @NonNull
    private AnilistService anilistService;

    public ProgramServices(
            @NonNull AnilistService anilistService, @NonNull AnimeService source,
            @NonNull YtDlpService ytDlpService) {
        this.anilistService = anilistService;
        this.source = source;
        this.ytDlpService = ytDlpService;
    }

    @NonNull
    public AnimeService getSource() {
        return source;
    }

    @NonNull
    public YtDlpService getYtDlpService() {
        return ytDlpService;
    }

    @NonNull
    public AnilistService getAnilistService() {
        return anilistService;
    }

}
