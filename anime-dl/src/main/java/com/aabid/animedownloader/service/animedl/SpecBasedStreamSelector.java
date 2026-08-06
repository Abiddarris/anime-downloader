package com.aabid.animedownloader.service.animedl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private @NonNull List<@NonNull QualitySpec> qualitySpecs;

    public SpecBasedStreamSelector(@NonNull List<@NonNull ServerSpec> serverSpecs,
                                   @NonNull List<@NonNull QualitySpec> qualitySpecs) {
        Objects.requireNonNull(serverSpecs, "specs can not be null");
        Objects.requireNonNull(qualitySpecs, "specs can not be null");

        this.qualitySpecs = validateQualitySpecs(qualitySpecs);
        this.serverSpecs = validateServerSpecs(serverSpecs);

    }

    @NonNull
    private List<@NonNull ServerSpec> validateServerSpecs(@NonNull List<@NonNull ServerSpec> specs) {
        List<@NonNull ServerSpec> serverSpecs = new ArrayList<>(specs);
        if (serverSpecs.isEmpty()) {
            serverSpecs.add(ServerSpec.ANY);
            return serverSpecs;
        }

        ensureOnTheLastElement(serverSpecs, ServerSpec.ANY);
        ensureOnTheLastElement(serverSpecs, ServerSpec.NONE);

        ServerSpec spec = serverSpecs.get(serverSpecs.size() - 1);
        if (!spec.equals(ServerSpec.ANY) && !spec.equals(ServerSpec.NONE)) {
            serverSpecs.add(ServerSpec.NONE);
        }

        return serverSpecs;
    }

    @NonNull
    private List<@NonNull QualitySpec> validateQualitySpecs(List<@NonNull QualitySpec> specs) {
        List<@NonNull QualitySpec> qualitySpecs = new ArrayList<>(specs);
        if (qualitySpecs.isEmpty()) {
            qualitySpecs.add(QualitySpec.ANY);
            return qualitySpecs;
        }

        ensureOnTheLastElement(qualitySpecs, QualitySpec.ANY);
        ensureOnTheLastElement(qualitySpecs, QualitySpec.BEST);
        ensureOnTheLastElement(qualitySpecs, QualitySpec.WORST);
        ensureOnTheLastElement(qualitySpecs, QualitySpec.NONE);

        QualitySpec spec = qualitySpecs.get(qualitySpecs.size() - 1);
        if (!(spec.equals(QualitySpec.ANY) || spec.equals(QualitySpec.NONE) ||
              spec.equals(QualitySpec.BEST) || spec.equals(QualitySpec.WORST))) {
            qualitySpecs.add(QualitySpec.NONE);
        }

        return qualitySpecs;
    }

    private static <T> void ensureOnTheLastElement(@NonNull List<T> collection, @Nullable T element) {
        int lastIndex = collection.size() - 1;
        if (collection.contains(element) && collection.indexOf(element) != lastIndex) {
            throw new IllegalArgumentException(element + " can only appear on the last element");
        }
    }

    @Override
    public @Nullable Selection select(@NonNull Episode episode) throws IOException, AnimeServiceException {
        List<@NonNull ServerInfo> candidateServers = getCandidateServers(episode);
        Map<@NonNull ServerInfo, Server> servers = new HashMap<>();
        for (QualitySpec qualitySpec : qualitySpecs) {
            if (qualitySpec.equals(QualitySpec.NONE)) {
                break;
            }

            if (qualitySpec.equals(QualitySpec.BEST) || qualitySpec.equals(QualitySpec.WORST)) {
                return selectForBestOrWorst(episode, candidateServers, qualitySpec);
            }

            Selection selection = selectAnyOrSpecificQuality(
                episode, candidateServers, servers, qualitySpec);
            if (selection != null) {
                return selection;
            }
        }

        throw new DownloadException("No stream available for the selected quality");
    }

    @SuppressWarnings("null")
    @Nullable
    private Selection selectAnyOrSpecificQuality(
            @NonNull Episode episode, @NonNull List<@NonNull ServerInfo> candidateServers,
            @NonNull Map<@NonNull ServerInfo, Server> servers, @NonNull QualitySpec qualitySpec) {
        for (ServerInfo serverInfo : candidateServers) {
            Server server = fetchServer(episode, servers, serverInfo);
            if (server == null) {
                continue;
            }

            if (qualitySpec.equals(QualitySpec.ANY)) {
                Optional<Quality> quality = server.getQualities()
                        .stream()
                        .findFirst();

                if (!quality.isPresent()) {
                    continue;
                }

                return new Selection(server, quality.get());
            }

            Optional<Quality> quality = server.getQuality(qualitySpec.getName());
            if (quality.isPresent()) {
                return new Selection(server, quality.get());
            }
        }

        return null;
    }

    @Nullable
    private Server fetchServer(@NonNull Episode episode, Map<@NonNull ServerInfo, Server> servers,
            @NonNull ServerInfo serverInfo) {
        Server server = servers.computeIfAbsent(serverInfo, info -> {
            try {
                return episode.fetchServer(serverInfo);
            } catch (IOException | AnimeServiceException e) {
                log.warn("Fail to fetch {} server", serverInfo.getId());
            }

            return null;
        });

        // Important to prevent failed servers from being refetched.
        if (server == null) {
            servers.put(serverInfo, null);
        }
        return server;
    }

    private Selection selectForBestOrWorst(@NonNull Episode episode,
            @NonNull List<@NonNull ServerInfo> allowedServers, @NonNull QualitySpec qualitySpec) throws IOException, AnimeServiceException {
        ListMultimap<Integer, Selection> candidateSelection =
                Multimaps.newListMultimap(new TreeMap<>(), ArrayList::new);

        for (ServerInfo serverInfo : allowedServers) {
            try {
                Server server = episode.fetchServer(serverInfo);
                for (Quality quality : server.getQualities()) {
                    int resolution = getResolution(quality);
                    candidateSelection.put(resolution, new Selection(server, quality));
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
        int resolution;
        if (qualitySpec.equals(QualitySpec.BEST)) {
            resolution = resolutions.get(resolutions.size() - 1);
        } else {
            resolution = resolutions.get(0);
        }

        return candidateSelection.get(resolution).get(0);
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
    private List<@NonNull ServerInfo> getCandidateServers(@NonNull Episode episode) throws IOException, AnimeServiceException {
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
