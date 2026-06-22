package com.aabid.animedownloader.cli;

import java.util.concurrent.Callable;

import com.aabid.animedownloader.m3u8.M3U8Downloader;
import com.aabid.animedownloader.source.AnimeSource;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "anime-downloader",
    description = "Download anime from tryembed.us.cc",
    mixinStandardHelpOptions = true,
    version = "1.0.0"
)
public class AnimeDownloader implements Callable<Integer> {

    @Option(names = {"-s", "--server"}, description = "ID of server to download from")
    private String serverId;

    @Option(names = {"-o", "--output"}, description = "Output file name", defaultValue = "output.mp4")
    private String output;

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

    @Parameters(index = "2", description = "Video resolution (e.g. 1080p, 720p, 480p)")
    private String quality;

    private AnimeSource source;
    private M3U8Downloader downloader;

    public AnimeDownloader(AnimeSource source, M3U8Downloader downloader) {
        this.source = source;
        this.downloader = downloader;
    }

    @Override
    public Integer call() throws Exception {
        Episode episode = source.queryAnime(animeId, episodeId);
        Server server = getServer(serverId, episode);
        if (!server.isReady()) {
            source.fetchServer(server);
        }

        for (Quality q : server.getQualities()) {
            if (!q.getName().contains(quality)) {
                continue;
            }

            if (!q.isResolved()) {
                source.resolveQuality(q, episode.getSourceLink());
            }

            downloader.download(q.getLink(), output, System.out, System.err);
            return 0;
        }


        System.out.println("Video not found.");
        return -1;
    }

    private static Server getServer(String serverId, Episode episode) {
        Server server = serverId != null ? episode.findServerById(serverId) : episode.getReadyServer();
        if (serverId == null && server == null) {
            System.err.println("No server available");
            System.exit(1);
        }

        if (serverId != null && server == null) {
            System.err.printf("No such server with id '%s'\n", serverId);
            System.exit(1);
        }

        return server;
    }

}
