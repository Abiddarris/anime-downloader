package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.m3u8.M3U8Downloader;
import com.aabid.animedownloader.source.AnimeSource;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class SubcommandFactory implements IFactory {

    private AnimeSource source;
    private M3U8Downloader downloader;

    public SubcommandFactory(AnimeSource source, M3U8Downloader downloader) {
        this.source = source;
        this.downloader = downloader;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == InfoSubcommand.class) {
            return cls.cast(new InfoSubcommand(source));
        } else if (cls == DownloadSubcommand.class) {
            return cls.cast(new DownloadSubcommand(source, downloader));
        }

        return CommandLine.defaultFactory().create(cls);
    }

}
