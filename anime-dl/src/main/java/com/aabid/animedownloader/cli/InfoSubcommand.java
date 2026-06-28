package com.aabid.animedownloader.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.source.AnimeNotFoundException;
import com.aabid.animedownloader.source.AnimeService;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;
import com.aabid.animedownloader.source.ServerInfo;

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
            episode = source.queryEpisode(animeId, episodeId);
        } catch (AnimeNotFoundException e) {
            err.println(e.getMessage());
            return 1;
        }

        List<Server> servers = episode.getServers()
            .stream()
            .map(server -> fetchServer(episode, server))
            .filter(Objects::nonNull)
            .toList();

        String formatting = "%-4s %-13s %-13s %-8s\n";
        out.printf(formatting, "No", "Server Name", "Server Id", "Quality");
        out.println("-".repeat(45));
        int no = 0;

        for (Server server : servers) {
            ServerInfo serverInfo = server.getInfo();
            for (Quality quality : server.getQualities()) {
                out.printf(formatting, ++no, serverInfo.getName(), serverInfo.getId(), quality.getName());
            }
        }

        return 0;
    }

    @Nullable
    private Server fetchServer(@NonNull Episode episode, @NonNull ServerInfo server) {
        try {
            return episode.fetchServer(server);
        } catch (IOException e) {
            log.warn("Failed to fetch qualities for server: {}", server.getId(), e);
            return null;
        }
    }

}
