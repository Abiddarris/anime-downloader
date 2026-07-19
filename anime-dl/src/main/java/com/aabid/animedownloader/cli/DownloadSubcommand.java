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
package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.anime.AnimeNotFoundException;
import com.aabid.animedownloader.anime.ServerException;
import com.aabid.animedownloader.cli.converter.OutputFormatterConverter;
import com.aabid.animedownloader.service.animedl.DownloadException;
import com.aabid.animedownloader.service.animedl.DownloadRequest;
import com.aabid.animedownloader.service.animedl.DownloadService;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;
import com.aabid.animedownloader.utils.format.NewFormatter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "download",
    description = "Download an anime episode from tryembed.us.cc",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class DownloadSubcommand extends BaseSubcommand {

    @Option(names = {"-s", "--server"}, description = "ID of server to download from")
    private String serverId;

    @Option(
        names = {"-o", "--output"},
        description = "Output file name (default: ${DEFAULT-VALUE})",
        defaultValue = "{anime_title} #{episode} [{id}].{ext}",
        converter = OutputFormatterConverter.class,
        paramLabel = "output"
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

    public DownloadSubcommand(ProgramServicesFactory factory) {
        super(factory);
    }

    @Override
    protected int start(ProgramServices services) throws Exception {
        DownloadService service = new DownloadService(services);
        try {
            DownloadRequest request = new DownloadRequest.Builder()
                .setEpisodeId(episodeId)
                .setAnimeId(animeId)
                .setServerId(serverId)
                .setQualityName(quality)
                .setFormatter(formatter)
                .setSimulate(simulate)
                .build();
            service.download(request);

            return 0;
        } catch (DownloadException e) {
            printError(e.getMessage());
        } catch (AnimeNotFoundException e) {
            printError(e.getMessage());
            printStackTrace(e);
        } catch (ServerException e) {
            printError(
                "Server unavailable or returned an error. " +
                "Use --verbose for more details or try switching to another server using the --server flag"
            );
            printStackTrace(e);
        }
        return 1;
    }
}
