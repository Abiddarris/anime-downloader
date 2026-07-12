package com.aabid.animedownloader.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeNotFoundException;
import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "info",
    description = "Fetch and display available servers and qualities for an episode",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class InfoSubcommand extends BaseSubcommand {

    private static final Logger log = LoggerFactory.getLogger(InfoSubcommand.class);

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

    public InfoSubcommand(@NonNull ProgramServicesFactory factory) {
        super(factory);
    }

    @Override
    protected int start(ProgramServices services) throws Exception {
        try {
            return printEpisodeInfo(services);
        } catch (AnimeNotFoundException e) {
            printError(e.getMessage());
            printStackTrace(e);
        } catch (AnimeServiceException e) {
            printError(
                "Failed to fetch episode information. " +
                "Use --verbose for more details or try again later."
            );
            printStackTrace(e);
        }

        return -1;
    }

    private int printEpisodeInfo(ProgramServices services) throws IOException, AnimeServiceException {
        PrintWriter out = services.getOut();
        AnimeService anime = services.getSource();

        Episode episode = anime.queryEpisode(animeId, episodeId);
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
        } catch (IOException | AnimeServiceException e) {
            log.warn("Failed to fetch qualities for server: {}", server.getId());
            log.debug("Detailed Stacktrace: ", e);
            return null;
        }
    }

}
