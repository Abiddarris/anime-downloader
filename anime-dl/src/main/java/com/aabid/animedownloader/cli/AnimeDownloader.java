package com.aabid.animedownloader.cli;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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

    @Option(names = {"-Q", "--quality"}, description = "Video resolution (e.g. 1080p, 720p, 480p)")
    private String quality;

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

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

        Optional<Quality> qualityOpt = getQuality(server, this.quality);
        if (qualityOpt.isEmpty()) {
            System.err.println("No stream found.");
            return -1;
        }

        Quality quality = qualityOpt.get();
        if (!quality.isResolved()) {
            source.resolveQuality(quality, episode.getSourceLink());
        }

        downloader.download(quality.getLink(), output, System.out, System.err);
        return 0;
    }

    private static Optional<Quality> getQuality(@NonNull Server server, @Nullable String quality) {
        if (quality == null) {
            return server.getQualities()
                .stream()
                .findFirst();
        }

        for (Quality q : server.getQualities()) {
            if (q.getName().contains(quality)) {
                return Optional.of(q);
            }
        }

        return Optional.empty();
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
