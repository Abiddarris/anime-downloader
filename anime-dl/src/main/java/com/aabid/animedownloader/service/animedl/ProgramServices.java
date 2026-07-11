package com.aabid.animedownloader.service.animedl;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.service.anilist.AnilistService;
import com.aabid.animedownloader.service.ytdlp.YtDlp;

public class ProgramServices {

    @NonNull
    private AnimeService source;

    @NonNull
    private YtDlp ytDlpService;

    @NonNull
    private AnilistService anilistService;

    public ProgramServices(
            @NonNull AnilistService anilistService, @NonNull AnimeService source,
            @NonNull YtDlp ytDlpService) {
        this.anilistService = anilistService;
        this.source = source;
        this.ytDlpService = ytDlpService;
    }

    @NonNull
    public AnimeService getSource() {
        return source;
    }

    @NonNull
    public YtDlp getYtDlpService() {
        return ytDlpService;
    }

    @NonNull
    public AnilistService getAnilistService() {
        return anilistService;
    }

}
