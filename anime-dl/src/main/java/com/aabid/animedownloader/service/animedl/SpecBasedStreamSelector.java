package com.aabid.animedownloader.service.animedl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerException;
import com.aabid.animedownloader.anime.ServerInfo;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

public class SpecBasedStreamSelector implements StreamSelector {

    private static final Logger log = LoggerFactory.getLogger(SpecBasedStreamSelector.class);

    private @NonNull List<@NonNull ServerSpec> serverSpecs;
    private @NonNull QualitySpec qualitySpec;

    public SpecBasedStreamSelector(@NonNull List<@NonNull ServerSpec> serverSpecs, @NonNull QualitySpec qualitySpec) {
        Objects.requireNonNull(serverSpecs, "specs can not be null");

        this.qualitySpec = qualitySpec;
        this.serverSpecs = getServerSpecs(serverSpecs);

    }

    @NonNull
    private List<@NonNull ServerSpec> getServerSpecs(List<@NonNull ServerSpec> specs) {
        List<@NonNull ServerSpec> serverSpecs = new ArrayList<>(specs);
        if (serverSpecs.isEmpty()) {
            serverSpecs.add(ServerSpec.ANY);
            return serverSpecs;
        }

        int lastIndex = serverSpecs.size() - 1;
        if ((serverSpecs.indexOf(ServerSpec.ANY) > 0 && serverSpecs.indexOf(ServerSpec.ANY) != lastIndex) ||
                (serverSpecs.indexOf(ServerSpec.NONE) > 0 && serverSpecs.indexOf(ServerSpec.NONE) != lastIndex)) {
            throw new IllegalArgumentException("ANY and NONE can only appear on the last element");
        }

        ServerSpec spec = serverSpecs.get(lastIndex);
        if (!spec.equals(ServerSpec.ANY) && !spec.equals(ServerSpec.NONE)) {
            serverSpecs.add(ServerSpec.NONE);
        }

        return serverSpecs;
    }

    @Override
    public @Nullable Selection select(@NonNull Episode episode) throws IOException, AnimeServiceException {
        List<@NonNull ServerInfo> allowedServers = resolveServers(episode);
        if (qualitySpec.equals(QualitySpec.ANY)) {
            return selectAnyQuality(episode, allowedServers);
        }

        if (qualitySpec.equals(QualitySpec.BEST) || qualitySpec.equals(QualitySpec.WORST)) {
            return selectForBestOrWorst(episode, allowedServers);
        }

        return selectSpecificQuality(episode, allowedServers);
    }

    @SuppressWarnings("null")
    private static Selection selectAnyQuality(@NonNull Episode episode, @NonNull List<@NonNull ServerInfo> selectedServer)
            throws IOException, AnimeServiceException {
        for (ServerInfo serverInfo : selectedServer) {
            try {
                Server server = episode.fetchServer(serverInfo);
                Optional<Quality> quality = server.getQualities()
                        .stream()
                        .findFirst();

                if (quality.isPresent()) {
                    return new Selection(serverInfo, quality.get());
                }
            } catch (ServerException e) {
                log.warn("Fail to fetch {} server", serverInfo.getId());
            }
        }

        throw new DownloadException("No stream available for the selected quality");
    }

    @SuppressWarnings("null")
    private Selection selectSpecificQuality(@NonNull Episode episode, List<@NonNull ServerInfo> allowedServers)
            throws IOException, AnimeServiceException {
        for (ServerInfo serverInfo : allowedServers) {
            try {
                Server server = episode.fetchServer(serverInfo);
                Optional<Quality> quality = server.getQuality(qualitySpec.getName());

                if (quality.isPresent()) {
                    return new Selection(serverInfo, quality.get());
                }
            } catch (ServerException e) {
                log.warn("Fail to fetch {} server", serverInfo.getId());
            }
        }

        throw new DownloadException("No stream available for the selected quality");
    }

    private Selection selectForBestOrWorst(@NonNull Episode episode,
            @NonNull List<@NonNull ServerInfo> allowedServers) throws IOException, AnimeServiceException {
        ListMultimap<Integer, Selection> candidateSelection =
                Multimaps.newListMultimap(new TreeMap<>(), ArrayList::new);
        for (ServerInfo serverInfo : allowedServers) {
            try {
                Server server = episode.fetchServer(serverInfo);
                for (Quality quality : server.getQualities()) {
                    int resolution = getResolution(quality);
                    candidateSelection.put(resolution, new Selection(serverInfo, quality));
                }
            } catch (ServerException e) {
                log.warn("Fail to fetch {} server", serverInfo.getId());
            }
        }

        if (candidateSelection.isEmpty()) {
            throw new DownloadException("No stream available for the selected quality");
        }

        log.debug("candidates selection: {}", candidateSelection) ;

        List<Integer> resolutions = new ArrayList<>(candidateSelection.keySet());
        if (qualitySpec.equals(QualitySpec.BEST)) {
            return candidateSelection.get(resolutions.get(resolutions.size() - 1)).get(0);
        }

        return candidateSelection.get(resolutions.get(0)).get(0);
    }

    private static int getResolution(Quality quality) {
        String name = quality.getName();
        if (name.endsWith("p")) {
            name = name.substring(0, name.length() - 1);
        }

        int res = 0;
        try {
            res = Integer.parseInt(name);
        } catch (NumberFormatException e) {
        }
        return res;
    }

    @NonNull
    private List<@NonNull ServerInfo> resolveServers(@NonNull Episode episode) throws IOException, AnimeServiceException {
        List<ServerInfo> availableServers = new ArrayList<>(episode.getServers());
        List<@NonNull ServerInfo> candidateServers = new ArrayList<>();
        for (ServerSpec serverSpec : serverSpecs) {
            if (serverSpec.equals(ServerSpec.NONE)) {
                break;
            }

            if (serverSpec.equals(ServerSpec.ANY)) {
                candidateServers.addAll(availableServers);
                break;
            }

            Optional<ServerInfo> info = episode.findServerById(serverSpec.getName());
            if (info.isEmpty()) {
                throw new DownloadException(String.format("Server '%s' not found", serverSpec));
            }

            ServerInfo serverInfo = info.get();
            availableServers.remove(serverInfo);
            candidateServers.add(serverInfo);
        }

        log.debug("Server spec: {}", serverSpecs);
        log.debug("Candidate server: {}", candidateServers);

        return candidateServers;
    }
}
