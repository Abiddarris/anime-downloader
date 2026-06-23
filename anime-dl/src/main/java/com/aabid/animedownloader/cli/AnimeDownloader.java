package com.aabid.animedownloader.cli;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "anime-downloader",
    description = "Download anime from tryembed.us.cc",
    mixinStandardHelpOptions = true,
    subcommands = {DownloadSubcommand.class, InfoSubcommand.class},
    version = "1.0.0"
)
public class AnimeDownloader implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        CommandLine.usage(this, System.out);
        return 2;
    }

}
