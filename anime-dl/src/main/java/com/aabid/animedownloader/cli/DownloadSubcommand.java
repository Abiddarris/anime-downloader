package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.anime.AnimeNotFoundException;
import com.aabid.animedownloader.anime.ServerException;
import com.aabid.animedownloader.cli.converter.OutputFormatterConverter;
import com.aabid.animedownloader.service.animedl.DownloadException;
import com.aabid.animedownloader.service.animedl.DownloadService;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;
import com.aabid.animedownloader.utils.format.NewFormatter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
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

    public DownloadSubcommand(ProgramServicesFactory factory) {
        super(factory);
    }

    @Override
    protected int start(ProgramServices services) throws Exception {
        DownloadService service = new DownloadService(services);
        try {
            service.download(
                episodeId, animeId, serverId, quality, formatter, simulate
            );
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
