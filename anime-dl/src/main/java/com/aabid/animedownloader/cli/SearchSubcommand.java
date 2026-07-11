package com.aabid.animedownloader.cli;

import static picocli.CommandLine.Help.Ansi.AUTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.service.anilist.AnilistService;
import com.aabid.animedownloader.service.anilist.AnimeEntry;
import com.aabid.animedownloader.service.animedl.ProgramConfiguration;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Command(
    name = "search",
    description = "Search for anime on AniList by name or keyword.",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class SearchSubcommand implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(SearchSubcommand.class);

    @Spec
    private CommandSpec spec;

    @Mixin
    private LoggingMixIn logging;

    @Mixin
    private TimeoutMixIn timeoutMixIn;

    @Option(
        names = { "--page", "-p" },
        description = "The page number of search results to display (default: ${DEFAULT-VALUE}).",
        defaultValue = "1"
    )
    private int page;

    @Parameters(
        index = "0",
        description = "The name or keyword of the anime you want to find."
    )
    private String keyword;

    private AnilistService service;

    @NonNull
    private final ProgramServicesFactory factory;

    public SearchSubcommand(ProgramServicesFactory factory) {
        this.factory = factory;
    }

    @Override
    public Integer call() throws Exception {
        logging.configureLogging();

        ProgramConfiguration.Builder builder = new ProgramConfiguration.Builder();
        timeoutMixIn.applyConfiguration(builder);

        ProgramServices services = factory.apply(builder.build());
        service = services.getAnilistService();

        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();

        try {
            return search(out);
        } catch (Exception e) {
            err.println(AUTO.string("@|red,bold " + e.toString() + "|@"));
            log.debug("Detailed Stacktrace: ", e);
            return 1;
        }
    }

    private Integer search(PrintWriter out) throws IOException {
        List<AnimeEntry> result = service.search(keyword, page);
        out.println(AUTO.string("@|yellow,bold \nSearch Results for:|@ '" + keyword + "' (Page " + page + ")\n"));

        for (int i = 0; i < result.size(); i++) {
            AnimeEntry entry = result.get(i);

            String episodesStr = (entry.getEpisodeCount() != null) ? entry.getEpisodeCount() + " eps" : "? eps";
            String primaryTitle = entry.getEnglishTitle().isBlank() ? entry.getRomajiTitle() : entry.getEnglishTitle();

            out.printf(AUTO.string("[%d] @|green %s|@ (%s) [%s] @|faint (ID: %d)|@%n"),
                    (i + 1),
                    primaryTitle,
                    entry.getFormat(),
                    episodesStr,
                    entry.getId()
            );

            if (!primaryTitle.equalsIgnoreCase(entry.getRomajiTitle()) && !entry.getRomajiTitle().isBlank()) {
                out.printf(AUTO.string("    @|faint %s|@%n"), entry.getRomajiTitle());
            }
        }

        out.println();
        return 0;
    }

}
