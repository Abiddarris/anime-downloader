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
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeService;
import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Caption;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.service.ytdlp.DownloadConfiguration;
import com.aabid.animedownloader.service.ytdlp.HttpException;
import com.aabid.animedownloader.service.ytdlp.Retries;
import com.aabid.animedownloader.service.ytdlp.YtDlp;
import com.aabid.animedownloader.service.ytdlp.YtDlpInvocationException;
import com.google.common.io.Files;

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

        Selection selection = request.getStreamSelector().select(episode);
        Quality quality = selection.getQuality();

        log.debug("Using quality: {}", quality);
        out.printf("Resolving stream link for '%s'%n", quality.getName());

        String link = episode.resolveQuality(quality);

        out.printf(
            "Fetching anime metadata for %d (AniList ID)%n",
            request.getEpisodeId(), request.getAnimeId()
        );

        String output = request.getOutputNameGenerator().generate(episodeInfo, selection);

        out.println("Passing stream link to yt-dlp for download");

        if (request.isSimulate()) {
            return;
        }

        Path video = invokeYtDlp(request, link, Path.of(output));
        downloadSubtitle(episode, selection.getServer(), video);
    }

    private void downloadSubtitle(@NonNull Episode episode, @NonNull Server server, @NonNull Path video)
            throws IOException, AnimeServiceException {
        List<@NonNull Caption> captions = server.getCaptions();
        if (captions.isEmpty()) {
            log.debug("No subtitle available");
            return;
        }

        out.println("Downloading subtitle...");
        for (Caption caption : captions) {
            String name = Files.getNameWithoutExtension(video.toString()) +
                "." + caption.getId() + "." + caption.getFormat();
            Path dest = video.resolveSibling(name);

            byte[] data = episode.downloadCaption(caption);

            log.debug("Writing {} subs to {}", caption.getName(), dest);
            java.nio.file.Files.write(dest, data);
        }

    }

    private Path invokeYtDlp(DownloadRequest request, String url, Path dest) throws IOException, YtDlpInvocationException,
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
            .setOverwrite(request.isOverwrite())
            .setFragmentRetries(Retries.infinite())
            .setBuffersize(1024 * 16)
            .build();

        DownloadProgressPrinter printer = new DownloadProgressPrinter(out);
        return ytDlpService.download(configuration, url, dest, printer);
    }


}
