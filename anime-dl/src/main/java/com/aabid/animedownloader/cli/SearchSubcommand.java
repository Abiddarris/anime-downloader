package com.aabid.animedownloader.cli;

import static picocli.CommandLine.Help.Ansi.AUTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.aabid.animedownloader.service.anilist.AnilistService;
import com.aabid.animedownloader.service.anilist.AnimeEntry;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "search",
    description = "Search for anime on AniList by name or keyword.",
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class
)
public class SearchSubcommand extends BaseSubcommand {

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

    public SearchSubcommand(ProgramServicesFactory factory) {
        super(factory);
    }

    @Override
    protected int start(ProgramServices services) throws Exception {
        return search(services);
    }

    private int search(ProgramServices services) throws IOException {
        AnilistService service = services.getAnilistService();
        PrintWriter out = services.getOut();

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
