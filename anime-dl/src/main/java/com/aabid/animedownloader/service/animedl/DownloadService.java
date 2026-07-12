package com.aabid.animedownloader.service.animedl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.service.ytdlp.DownloadConfiguration;
import com.aabid.animedownloader.service.ytdlp.HttpException;
import com.aabid.animedownloader.service.ytdlp.Retries;
import com.aabid.animedownloader.service.ytdlp.YtDlp;
import com.aabid.animedownloader.service.ytdlp.YtDlpInvocationException;
import com.aabid.animedownloader.utils.format.NewFormatter;

public class DownloadService {

    private static final Logger log = LoggerFactory.getLogger(DownloadService.class);

    private @NonNull AnimeService source;

    private @NonNull YtDlp ytDlpService;

    private @NonNull PrintWriter out;

    private @NonNull UserAgentProvider userAgentProvider;

    public DownloadService(@NonNull ProgramServices services) {
        this.source = services.getSource();
        this.ytDlpService = services.getYtDlpService();
        this.out = services.getOut();
        this.userAgentProvider = services.getUserAgentProvider();
    }

    public void download(DownloadRequest request) throws IOException, AnimeServiceException, YtDlpInvocationException,
                                                  InterruptedException, HttpException {
        out.printf(
            "Fetching episode %d for anime %d (AniList ID)%n",
            request.getEpisodeId(), request.getAnimeId()
        );

        Episode episode = source.queryEpisode(request.getAnimeId(), request.getEpisodeId());
        EpisodeInfo episodeInfo = episode.getEpisodeInfo();

        out.printf("Found: %s — Episode %d%n", episodeInfo.getAnimeTitle(), request.getEpisodeId());

        Server server = getServer(episode, request.getServerId());
        Quality quality = getQuality(server, request.getQualityName());

        log.debug("Using quality: {}", quality);
        out.printf("Resolving stream link for '%s'%n", quality.getName());

        String link = episode.resolveQuality(quality);
        String output = getOutputName(request.getFormatter(), episodeInfo, server.getInfo(), quality);

        out.println("Passing stream link to yt-dlp for download");

        if (!request.isSimulate()) {
            invokeYtDlp(link, Path.of(output));
        }
    }

    private static Server getServer(Episode episode, String serverId) throws IOException, AnimeServiceException {
        if (serverId != null) {
            Optional<ServerInfo> info = episode.findServerById(serverId);
            if (info.isEmpty()) {
                throw new DownloadException(String.format("Server '%s' not found", serverId));
            }

            return episode.fetchServer(info.get());
        }

        Optional<ServerInfo> info = episode.getReadyServer();
        if (info.isEmpty()) {
            throw new DownloadException("No servers available");
        }

        return episode.fetchServer(info.get());
    }

    private static Quality getQuality(@NonNull Server server, @Nullable String qualityName) {
        Supplier<DownloadException> exception =
            () -> new DownloadException("No stream available for the selected quality");
        if (qualityName != null) {
            return server.getQuality(qualityName)
                    .orElseThrow(exception);
        }

        log.debug("No --quality specified, using first available quality");

        return server.getQualities()
                .stream()
                .findFirst()
                .orElseThrow(exception);
    }

    private void invokeYtDlp(String url, Path dest) throws IOException, YtDlpInvocationException,
             InterruptedException, HttpException {
        List<String> headers = new ArrayList<>();
        headers.add("User-Agent: " + userAgentProvider.getUserAgent());
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

        DownloadProgressPrinter printer = new DownloadProgressPrinter(out);
        ytDlpService.download(configuration, url, dest, printer);
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

}
