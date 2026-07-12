package com.aabid.animedownloader.service.animedl;

import java.io.PrintWriter;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.service.anilist.AnilistService;
import com.aabid.animedownloader.service.ytdlp.YtDlp;

/**
 * Container class for holding all program services.
 * Provides access to anime source service, YtDlp service, and Anilist service.
 */
public class ProgramServices {

    @NonNull
    private AnimeService source;

    @NonNull
    private YtDlp ytDlpService;

    @NonNull
    private AnilistService anilistService;

    @NonNull
    private PrintWriter out;

    @NonNull
    private PrintWriter err;

    @NonNull
    private UserAgentProvider userAgentProvider;

    public ProgramServices(
            @NonNull AnilistService anilistService, @NonNull AnimeService source,
            @NonNull YtDlp ytDlpService, @NonNull PrintWriter out, @NonNull PrintWriter err,
            @NonNull UserAgentProvider userAgentProvider) {
        this.anilistService = anilistService;
        this.source = source;
        this.ytDlpService = ytDlpService;
        this.out = out;
        this.err = err;
        this.userAgentProvider = userAgentProvider;
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

    @NonNull
    public PrintWriter getOut() {
        return out;
    }

    @NonNull
    public PrintWriter getErr() {
        return err;
    }

    @NonNull
    public UserAgentProvider getUserAgentProvider() {
        return userAgentProvider;
    }
}