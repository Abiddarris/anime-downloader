package com.aabid.animedownloader.cli;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anilist.AnilistService;
import com.aabid.animedownloader.m3u8.M3U8Downloader;
import com.aabid.animedownloader.source.AnimeSource;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class SubcommandFactory implements IFactory {

    @NonNull
    private AnimeSource source;

    @NonNull
    private M3U8Downloader downloader;

    @NonNull
    private AnilistService anilistService;

    public SubcommandFactory(
            @NonNull AnilistService anilistService, @NonNull AnimeSource source,
            @NonNull M3U8Downloader downloader) {
        this.anilistService = anilistService;
        this.source = source;
        this.downloader = downloader;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == InfoSubcommand.class) {
            return cls.cast(new InfoSubcommand(source));
        } else if (cls == DownloadSubcommand.class) {
            return cls.cast(new DownloadSubcommand(source, downloader));
        } else if (cls == SearchCommand.class) {
            return cls.cast(new SearchCommand(anilistService));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
