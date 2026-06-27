package com.aabid.animedownloader.cli;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.m3u8.M3U8Downloader;
import com.aabid.animedownloader.source.AnimeNotFoundException;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.AnimeService;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;
import com.aabid.animedownloader.source.Server.ServerState;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "download",
    description = "Download an anime episode from tryembed.us.cc",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class DownloadSubcommand implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(DownloadSubcommand.class);

    @Spec
    private CommandSpec spec;

    @Mixin
    private LoggingMixIn loggingMixIn;

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

    private AnimeService source;
    private M3U8Downloader downloader;

    public DownloadSubcommand(AnimeService source, M3U8Downloader downloader) {
        this.source = source;
        this.downloader = downloader;
    }

    @Override
    public Integer call() throws Exception {
        loggingMixIn.configureLogging();

        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();

        out.printf("Fetching episode %d for anime %d (AniList ID)%n", episodeId, animeId);

        Episode episode;
        try {
            episode = source.queryAnime(animeId, episodeId);
        } catch (AnimeNotFoundException e) {
            err.println(e.getMessage());
            return 1;
        }
        out.printf("Found: %s — Episode %d%n", episode.getMetadata().getAnimeTitle(), episodeId);

        Server server = serverId != null ? episode.findServerById(serverId) : episode.getReadyServer();
        if (server == null) {
            if (serverId == null) {
                err.println("No servers available");
            } else {
                err.printf("Server '%s' not found%n", serverId);
            }
            return 1;
        }

        if (server.getState() != ServerState.READY) {
            out.printf("Fetching available qualities from '%s'%n", server.getId());
            source.fetchServer(server);
        }

        Optional<Quality> qualityOpt = getQuality(server, this.quality);
        if (qualityOpt.isEmpty()) {
            err.println("No stream available for the selected quality");
            return 1;
        }

        Quality quality = qualityOpt.get();
        if (!quality.isResolved()) {
            out.printf("Resolving stream link for '%s'%n", quality.getName());
            source.resolveQuality(quality);
        }

        out.println("Passing stream link to yt-dlp for download");
        downloader.download(quality.getLink(), output, System.out, System.err);
        return 0;
    }

    private static Optional<Quality> getQuality(@NonNull Server server, @Nullable String qualityName) {
        Optional<Quality> quality;
        if (qualityName == null) {
            log.debug("No --quality specified, using first available quality");

            quality = server.getQualities()
                .stream()
                .findFirst();
        } else {
            quality = server.getQuality(qualityName);
        }

        log.debug("Using quality: {}", quality.orElse(null));
        return quality;
    }
}
