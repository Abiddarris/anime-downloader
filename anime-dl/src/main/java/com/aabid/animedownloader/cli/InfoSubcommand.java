package com.aabid.animedownloader.cli;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.aabid.animedownloader.source.AnimeSource;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "info",
    description = "Fetch and display available servers and qualities for an episode",
    mixinStandardHelpOptions = true
)
public class InfoSubcommand implements Callable<Integer> {

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
        Episode episode = source.queryAnime(animeId, episodeId);
        for (Server server : episode.getServers()) {
            if (!server.isReady()) {
                try {
                    source.fetchServer(server);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String formatting = "%-4s %-13s %-13s %-8s\n";
        System.out.printf(formatting, "No", "Server Name", "Server Id", "Quality");
        System.out.println("-".repeat(45));
        int no = 0;
        for (Server server : episode.getServers()) {
            if (!server.isReady()) {
                continue;
            }

            for (Quality quality : server.getQualities()) {
                System.out.printf(formatting, ++no, server.getName(), server.getId(), quality.getName());
            }
        }

        return 0;
    }

}
