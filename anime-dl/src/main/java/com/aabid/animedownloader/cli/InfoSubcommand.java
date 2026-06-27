package com.aabid.animedownloader.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.source.AnimeNotFoundException;
import com.aabid.animedownloader.source.AnimeService;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "info",
    description = "Fetch and display available servers and qualities for an episode",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class InfoSubcommand implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(InfoSubcommand.class);

    @Spec
    private CommandSpec spec;

    @Mixin
    private LoggingMixIn loggingMixIn;

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

    private AnimeService source;

    public InfoSubcommand(AnimeService source) {
        this.source = source;
    }

    @Override
    public Integer call() throws Exception {
        loggingMixIn.configureLogging();

        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getOut();

        Episode episode;
        try {
            episode = source.queryAnime(animeId, episodeId);
        } catch (AnimeNotFoundException e) {
            err.println(e.getMessage());
            return 1;
        }

        episode.getServers().forEach(server -> {
            switch (server.getState()) {
                case FAILED -> log.debug("Server {} is not available", server);
                case IDLE -> {
                    log.debug("Server {} is idle. Fetching available qualities...", server.getId());
                    try {
                        source.fetchServer(server);
                    } catch (IOException e) {
                        log.warn("Failed to fetch qualities for server: {}", server.getId(), e);
                    }
                    break;
                }
                case READY -> log.debug("Server {} is already fully initialized.", server.getId());
            }
        });

        String formatting = "%-4s %-13s %-13s %-8s\n";
        out.printf(formatting, "No", "Server Name", "Server Id", "Quality");
        out.println("-".repeat(45));
        int no = 0;
        for (Server server : episode.getReadyServers()) {
            for (Quality quality : server.getQualities()) {
                out.printf(formatting, ++no, server.getName(), server.getId(), quality.getName());
            }
        }

        return 0;
    }

}
