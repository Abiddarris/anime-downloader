/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aabid.animedownloader.service.animedl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerException;
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

        Selection selection =  select(episode, request.getServerId(), request.getQualityName());
        ServerInfo serverInfo = selection.getServerInfo();
        Quality quality = selection.getQuality();

        log.debug("Using quality: {}", quality);
        out.printf("Resolving stream link for '%s'%n", quality.getName());

        String link = episode.resolveQuality(quality);
        String output = getOutputName(request.getFormatter(), episodeInfo, serverInfo, quality);

        out.println("Passing stream link to yt-dlp for download");

        if (!request.isSimulate()) {
            invokeYtDlp(link, Path.of(output));
        }
    }

    private static Selection select(@NonNull Episode episode, @NonNull String serverId, @NonNull String qualityName)
            throws IOException, AnimeServiceException {
        List<ServerInfo> selectedServer = getSelectedServer(episode, serverId);
        for (ServerInfo serverInfo : selectedServer) {
            try {
                Server server = episode.fetchServer(serverInfo);
                Optional<Quality> quality;
                if (qualityName != null) {
                    quality = server.getQuality(qualityName);
                } else {
                    log.debug("No --quality specified, using first available quality");

                    quality = server.getQualities()
                        .stream()
                        .findFirst();
                }

                if (quality.isPresent()) {
                    return new Selection(serverInfo, quality.get());
                }
            } catch (ServerException e) {
                log.warn("Fail to fetch {} server", serverInfo.getId());
            }
        }

        throw new DownloadException("No stream available for the selected quality");
    }

    private static List<ServerInfo> getSelectedServer(Episode episode, String serverId) throws IOException, AnimeServiceException {
        if (serverId != null) {
            Optional<ServerInfo> info = episode.findServerById(serverId);
            if (info.isEmpty()) {
                throw new DownloadException(String.format("Server '%s' not found", serverId));
            }

            return List.of(info.get());
        }

        return episode.getServers();
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
