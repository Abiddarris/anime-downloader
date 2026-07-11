package com.aabid.animedownloader.cli;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
    name = "anime-downloader",
    description = "Download anime from tryembed.us.cc",
    mixinStandardHelpOptions = true,
    subcommands = {DownloadSubcommand.class, InfoSubcommand.class, SearchSubcommand.class},
    versionProvider = VersionProvider.class
)
public class AnimeDlCommand implements Callable<Integer> {

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() throws Exception {
        spec.commandLine().usage(System.out);
        return 2;
    }

}
