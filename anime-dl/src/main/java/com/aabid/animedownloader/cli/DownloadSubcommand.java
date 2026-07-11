package com.aabid.animedownloader.cli;

import static picocli.CommandLine.Help.Ansi.AUTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.cli.converter.OutputFormatterConverter;
import com.aabid.animedownloader.cli.mixin.LoggingMixIn;
import com.aabid.animedownloader.cli.mixin.TimeoutMixIn;
import com.aabid.animedownloader.service.animedl.ProgramConfiguration;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;
import com.aabid.animedownloader.service.ytdlp.DownloadConfiguration;
import com.aabid.animedownloader.service.ytdlp.HttpException;
import com.aabid.animedownloader.service.ytdlp.Retries;
import com.aabid.animedownloader.service.ytdlp.YtDlpInvocationException;
import com.aabid.animedownloader.service.ytdlp.YtDlp;
import com.aabid.animedownloader.utils.format.NewFormatter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
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

    @Mixin
    private TimeoutMixIn timeoutMixIn;

    @Option(names = {"-s", "--server"}, description = "ID of server to download from")
    private String serverId;

    @Option(
        names = {"-o", "--output"},
        description = "Output file name",
        defaultValue = "{anime_title} #{episode} [{id}].{ext}",
        converter = OutputFormatterConverter.class,
        paramLabel = "output",
        showDefaultValue = Visibility.ALWAYS
    )
    private NewFormatter formatter;

    @Option(names = {"-Q", "--quality"}, description = "Video resolution (e.g. 1080p, 720p, 480p)")
    private String quality;

    @Option(names = { "-S", "--simulate" }, description = "Do not download the video")
    private boolean simulate;

    @Parameters(index = "0", description = "AniList anime ID")
    private int animeId;

    @Parameters(index = "1", description = "Episode number")
    private int episodeId;

    private PrintWriter out;
    private PrintWriter err;

    private AnimeService source;
    private YtDlp ytDlpService;
    private ProgramServicesFactory factory;

    public DownloadSubcommand(ProgramServicesFactory factory) {
        this.factory = factory;
    }

    @Override
    public Integer call() throws Exception {
        loggingMixIn.configureLogging();

        ProgramConfiguration.Builder builder = new ProgramConfiguration.Builder();
        timeoutMixIn.applyConfiguration(builder);

        ProgramServices services = factory.apply(builder.build());
        source = services.getSource();
        ytDlpService = services.getYtDlpService();

        out = spec.commandLine().getOut();
        err = spec.commandLine().getErr();

        try {
            return download();
        } catch (Exception e) {
            err.println(AUTO.string("@|red,bold " + e.toString() + "|@"));
            log.debug("Detailed Stacktrace: ", e);
            return 1;
        }
    }

    private int download() throws Exception {
        out.printf("Fetching episode %d for anime %d (AniList ID)%n", episodeId, animeId);

        Episode episode = source.queryEpisode(animeId, episodeId);
        EpisodeInfo episodeInfo = episode.getEpisodeInfo();

        out.printf("Found: %s — Episode %d%n", episodeInfo.getAnimeTitle(), episodeId);

        Server server = null;
        if (serverId == null) {
            for (ServerInfo info : episode.getServers()) {
                try {
                    server = episode.fetchServer(info);
                    break;
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }
            }

            if (server == null) {
                err.println("No servers available");
                return 1;
            }
        } else {
            Optional<ServerInfo> info = episode.findServerById(serverId);
            if (info.isEmpty()) {
                err.printf("Server '%s' not found%n", serverId);
                return 1;
            }

            server = episode.fetchServer(info.get());
        }

        Optional<Quality> qualityOpt = getQuality(server, this.quality);
        if (qualityOpt.isEmpty()) {
            err.println("No stream available for the selected quality");
            return 1;
        }

        Quality quality = qualityOpt.get();

        out.printf("Resolving stream link for '%s'%n", quality.getName());

        String link = episode.resolveQuality(quality);
        String output = getOutputName(formatter, episodeInfo, server.getInfo(), quality);

        out.println("Passing stream link to yt-dlp for download");

        if (!simulate) {
            invokeYtDlp(link, Path.of(output));
        }

        return 0;
    }

    private void invokeYtDlp(String url, Path dest) throws IOException, YtDlpInvocationException,
             InterruptedException, HttpException {
        List<String> headers = new ArrayList<>();
        headers.add("User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0");
        headers.add("Accept: */*");
        headers.add("Accept-Language: en-US,en;q=0.9");
        // headers.add("Accept-Encoding: gzip, deflate, br, zstd");
        headers.add("Origin: https://tryembed.us.cc");
        headers.add("Referer: https://tryembed.us.cc/");
        headers.add("Connection: keep-alive");
        headers.add("Sec-Fetch-Dest: empty");
        headers.add("Sec-Fetch-Mode: cors");
        headers.add("Sec-Fetch-Site: cross-site");
        headers.add("TE: trailers");

        DownloadConfiguration configuration = new DownloadConfiguration.Builder()
            .setHeaders(headers)
            .setFragmentRetries(Retries.infinite())
            .setBuffersize(1024 * 16)
            .build();

        ProgressPrinter printer = new ProgressPrinter(out);
        ytDlpService.download(configuration, url, dest, printer::onProgressUpdate);
    }

    private String getOutputName(@NonNull NewFormatter formatter, @NonNull EpisodeInfo episodeInfo,
                                 @NonNull ServerInfo serverInfo, @NonNull Quality quality) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", episodeInfo.getAnilistId());
        metadata.put("episode", episodeInfo.getEpisode());
        metadata.put("anime_title", episodeInfo.getAnimeTitle());
        metadata.put("ext", "%(ext)s");
        metadata.put("server_name", serverInfo.getName());
        metadata.put("server_id", serverInfo.getId());
        metadata.put("quality", quality.getName());

        String output = formatter.format(metadata);
        log.debug("Output filename: {}", output);

        return output;
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
