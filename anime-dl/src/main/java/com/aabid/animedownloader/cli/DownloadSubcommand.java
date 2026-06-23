package com.aabid.animedownloader.cli;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.aabid.animedownloader.m3u8.M3U8Downloader;
import com.aabid.animedownloader.source.AnimeSource;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

@Command(
    name = "download",
    description = "Download an anime episode from tryembed.us.cc",
    mixinStandardHelpOptions = true
)
public class DownloadSubcommand implements Callable<Integer> {

    @Spec
    private CommandSpec spec;

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

    public DownloadSubcommand(AnimeSource source, M3U8Downloader downloader) {
        this.source = source;
        this.downloader = downloader;
    }

    @Override
    public Integer call() throws Exception {
        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();

        out.printf("Fetching episode %d for anime %d (AniList ID)%n", episodeId, animeId);
        Episode episode = source.queryAnime(animeId, episodeId);

        Server server = serverId != null ? episode.findServerById(serverId) : episode.getReadyServer();
        if (serverId == null && server == null) {
            err.println("No server available");
            return 1;
        }

        if (serverId != null && server == null) {
            err.printf("No such server with id '%s'\n", serverId);
            return 1;
        }

        if (!server.isReady()) {
            out.printf("Fetching available qualities from '%s'%n", server.getId());
            source.fetchServer(server);
        }

        Optional<Quality> qualityOpt = getQuality(server, this.quality);
        if (qualityOpt.isEmpty()) {
            err.println("No stream found.");
            return 1;
        }

        Quality quality = qualityOpt.get();
        if (!quality.isResolved()) {
            out.printf("Resolving stream link for '%s'%n", quality.getName());
            source.resolveQuality(quality, episode.getSourceLink());
        }

        out.println("Passing stream link to yt-dlp for download");
        downloader.download(quality.getLink(), output, System.out, System.err);
        return 0;
    }

    private static Optional<Quality> getQuality(@NonNull Server server, @Nullable String quality) {
        if (quality == null) {
            return server.getQualities()
                .stream()
                .findFirst();
        }

        return server.getQuality(quality);
    }
}
