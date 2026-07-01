package com.aabid.animedownloader.cli;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anilist.AnilistService;
import com.aabid.animedownloader.service.ytdlp.YtDlpService;
import com.aabid.animedownloader.source.AnimeService;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class SubcommandFactory implements IFactory {

    @NonNull
    private AnimeService source;

    @NonNull
    private YtDlpService ytDlpService;

    @NonNull
    private AnilistService anilistService;

    public SubcommandFactory(
            @NonNull AnilistService anilistService, @NonNull AnimeService source,
            @NonNull YtDlpService ytDlpService) {
        this.anilistService = anilistService;
        this.source = source;
        this.ytDlpService = ytDlpService;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == InfoSubcommand.class) {
            return cls.cast(new InfoSubcommand(source));
        } else if (cls == DownloadSubcommand.class) {
            return cls.cast(new DownloadSubcommand(source, ytDlpService));
        } else if (cls == SearchCommand.class) {
            return cls.cast(new SearchCommand(anilistService));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
