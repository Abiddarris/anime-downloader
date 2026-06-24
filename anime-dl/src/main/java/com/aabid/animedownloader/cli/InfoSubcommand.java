package com.aabid.animedownloader.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import com.aabid.animedownloader.source.AnimeSource;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@Command(
    name = "info",
    description = "Fetch and display available servers and qualities for an episode",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class InfoSubcommand implements Callable<Integer> {

    @Spec
    private CommandSpec spec;

    @Mixin
    private LoggingMixIn loggingMixIn;

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

    private AnimeSource source;

    public InfoSubcommand(AnimeSource source) {
        this.source = source;
    }

    @Override
    public Integer call() throws Exception {
        loggingMixIn.configureLogging();

        PrintWriter out = spec.commandLine().getOut();

        Episode episode = source.queryAnime(animeId, episodeId);
        for (Server server : episode.getServers()) {
            if (!server.isReady()) {
                try {
                    source.fetchServer(server);
                } catch (IOException e) {
                }
            }
        }

        String formatting = "%-4s %-13s %-13s %-8s\n";
        out.printf(formatting, "No", "Server Name", "Server Id", "Quality");
        out.println("-".repeat(45));
        int no = 0;
        for (Server server : episode.getServers()) {
            if (!server.isReady()) {
                continue;
            }

            for (Quality quality : server.getQualities()) {
                out.printf(formatting, ++no, server.getName(), server.getId(), quality.getName());
            }
        }

        return 0;
    }

}
