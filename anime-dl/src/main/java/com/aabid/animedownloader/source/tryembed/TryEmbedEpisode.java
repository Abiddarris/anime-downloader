package com.aabid.animedownloader.source.tryembed;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.source.AnimeNotFoundException;
import com.aabid.animedownloader.source.Episode;
import com.aabid.animedownloader.source.EpisodeInfo;
import com.aabid.animedownloader.source.Quality;
import com.aabid.animedownloader.source.Server;
import com.aabid.animedownloader.source.ServerInfo;
import com.aabid.animedownloader.source.tryembed.ApiResponse.Provider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

class TryEmbedEpisode extends Episode {

    private static final Logger log = LoggerFactory.getLogger(TryEmbedEpisode.class);

    @NonNull
    private final OkHttpClient client;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final UserAgentProvider userAgentProvider;

    @Nullable
    private String nonce;

    @NonNull
    private String source;

    @NonNull
    private EpisodeInfo info;

    @NonNull
    private List<ServerInfo> servers;

    private int animeId;
    private int episode;

	TryEmbedEpisode(@NotNull OkHttpClient client, @NonNull ObjectMapper mapper,
            @NonNull UserAgentProvider userAgentProvider, int animeId, int episode)
            throws IOException, AnimeNotFoundException {
        this.client = client;
        this.mapper = mapper;
        this.userAgentProvider = userAgentProvider;
        this.animeId = animeId;
        this.episode = episode;
        this.source = TryembedUrls.getEpisodeUrl(animeId, episode).toString();

        fetchCookiesAndRootNonce();

        ApiResponse response = fetchEpisodeData(null);
        this.info = ApiResponseParser.createEpisodeInfo(response);
        this.servers = ApiResponseParser.createServers(response.providers);
	}

    private void fetchCookiesAndRootNonce() throws IOException {
        Request request = new Request.Builder()
                .url(source)
                .addHeader("User-Agent", userAgentProvider.getUserAgent())
                .build();

        log.debug("Fetching cookies and root nonce from: {}", source);
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String page = response.body().string();
            int nonceStatementIndex = page.indexOf("window.EMBED_NONCE=\"");
            int nonceIndex = nonceStatementIndex + "window.EMBED_NONCE=\"".length();

            this.nonce = page.substring(nonceIndex, page.indexOf('"', nonceIndex));

            log.debug("Successfully updated cookies and root nonce.");
        }
    }

    @NonNull
    private ApiResponse fetchEpisodeData(@Nullable String server) throws IOException {
        log.debug(
            "Fetching episode stream metadata - animeId: {}, episode: {}, server: {}, referer: {} nonce: {}",
            animeId, episode, server, source, nonce
        );

        Request request = new Request.Builder()
                .url(TryembedUrls.getEpisodeApiUrl(animeId, episode, server, nonce))
                .addHeader("User-Agent", userAgentProvider.getUserAgent())
                .addHeader("X-Embed-Nonce", nonce)
                .addHeader("Referer", source)
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .build();

        log.debug("Executing API request to: {}", request.url());
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String apiResponseStr = response.body().string();
            log.debug("Received raw JSON response from API: {}", apiResponseStr);

            ApiResponse apiResponse = mapper.readValue(apiResponseStr, ApiResponse.class);
            this.nonce = apiResponse.embedNonce;

            return apiResponse;
        }
    }

    @Override
    public String resolveQuality(Quality quality) throws IOException {
        log.info("Resolving direct target file path for quality: {}", quality.getName());
        if (quality instanceof DirectQuality directQuality) {
            return directQuality.getLink();
        } else if (!(quality instanceof TokenBasedQuality)) {
            throw new AssertionError();
        }

        TokenBasedQuality tokenBasedQuality = (TokenBasedQuality) quality;
        Request request = new Request.Builder()
                .url(TryembedUrls.getTokenResolutionUrl(tokenBasedQuality.getToken()))
                .header("User-Agent", userAgentProvider.getUserAgent())
                .header("Accept", "*/*")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", source)
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("TE", "trailers")
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String realLink = response.header("Location");
            log.debug("Resolved direct mirror stream link: {}", realLink);

            return realLink;
        }

    }

    private void checkSuccessful(Request request, Response response) throws IOException {
        if (response.isRedirect()) {
            return;
        }

        if (!response.isSuccessful()) {
            throw new IOException(
                    "'" + request.url() + "' returns code " + response.code() + ": " + response.body().string());
        }
    }

    @Override
    public EpisodeInfo getEpisodeInfo() {
        return info;
    }

    @Override
    public List<ServerInfo> getServers() {
        return Collections.unmodifiableList(servers);
    }

    @Override
    public Server fetchServer(ServerInfo info) throws IOException {
        log.info("Fetching server: {}", info);

        ApiResponse response = fetchEpisodeData(info.getId());
        Provider provider = response.providers.stream()
                .filter(p -> p.id.equals(info.getId()))
                .findFirst()
                .get();

        return ApiResponseParser.createServer(info, provider);
    }
}
