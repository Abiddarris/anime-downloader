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
package com.aabid.animedownloader.service.tryembed;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.service.tryembed.ApiResponse.Provider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class TryEmbedEpisode extends Episode {

    private static final Logger log = LoggerFactory.getLogger(TryEmbedEpisode.class);

    @NonNull
    private final OkHttpClient client;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final UserAgentProvider userAgentProvider;

    @NonNull
    private String source;

    @NonNull
    private EpisodeInfo info;

    @NonNull
    private List<ServerInfo> servers;

    @Nullable
    private ServerInfo readyServer;

    @NonNull
    private NonceManager nonceManager;

    private int animeId;
    private int episode;

	TryEmbedEpisode(@NotNull OkHttpClient client, @NonNull ObjectMapper mapper,
            @NonNull UserAgentProvider userAgentProvider, int animeId, int episode)
            throws IOException, AnimeServiceException {
        this.client = client;
        this.mapper = mapper;
        this.userAgentProvider = userAgentProvider;
        this.animeId = animeId;
        this.episode = episode;
        this.source = TryembedUrls.getEpisodeUrl(animeId, episode).toString();
        this.nonceManager = new NonceManager(fetchCookiesAndRootNonce());

        ApiResponse response = fetchEpisodeData(null);
        this.info = ApiResponseParser.createEpisodeInfo(response);
        this.servers = ApiResponseParser.createServers(response.providers);
        this.readyServer = ApiResponseParser.getReadyServer(this.servers, response);
	}

    private String fetchCookiesAndRootNonce() throws IOException, AnimeServiceException {
        Request request = new Request.Builder()
                .url(source)
                .addHeader("User-Agent", userAgentProvider.getUserAgent())
                .build();

        log.debug("Fetching cookies and root nonce from: {}", source);

        return executeRequest(request, response -> {
            String page = response.body().string();
            int nonceStatementIndex = page.indexOf("window.EMBED_NONCE=\"");
            int nonceIndex = nonceStatementIndex + "window.EMBED_NONCE=\"".length();

            String nonce = page.substring(nonceIndex, page.indexOf('"', nonceIndex));

            log.debug("Successfully updated cookies and root nonce.");
            return nonce;
        });
    }

    @SuppressWarnings("unused")
    @NonNull
    private ApiResponse fetchEpisodeData(@Nullable String server) throws IOException, AnimeServiceException {
        String nonce = nonceManager.acquire();
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
        return executeRequest(request, response -> {
            ResponseBody body = response.body();
            if (body == null) {
                throw new AnimeServiceException("HTTP response body is null");
            }

            String apiResponseStr = body.string();
            log.debug("Received raw JSON response from API: {}", apiResponseStr);

            try {
                ApiResponse apiResponse = mapper.readValue(apiResponseStr, ApiResponse.class);
                if (apiResponse == null) {
                    throw new AnimeServiceException("Failed to parse API response; deserialized object is null");
                }
                nonceManager.update(apiResponse.embedNonce);

                return apiResponse;
            } catch (JacksonException e) {
                throw new AnimeServiceException("Failed to deserialize API response JSON", e);
            }
        });
    }

    @Override
    @NonNull
    public String resolveQuality(Quality quality) throws IOException, AnimeServiceException {
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

        return executeRequest(request, response -> {
            String realLink = response.header("Location");
            log.debug("Resolved direct mirror stream link: {}", realLink);

            return realLink;
        });

    }

    private <R> R executeRequest(Request request, ResponseConsumer<R> consumer) throws IOException, AnimeServiceException {
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            return consumer.consume(response);
        }
    }

    @FunctionalInterface
    private interface ResponseConsumer<R> {
        R consume(Response response) throws IOException, AnimeServiceException;
    }

    private void checkSuccessful(Request request, Response response) throws IOException, AnimeServiceException {
        if (response.isRedirect()) {
            return;
        }

        if (response.isSuccessful()) {
            return;
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new AnimeServiceException("'" + request.url() + "' returns code " + response.code());
        }

        String body = responseBody.string();
        String message = body;
        try {
            JsonNode tree = mapper.readTree(body);
            JsonNode errorNode = tree.get("error");
            if (errorNode != null) {
                message = errorNode.asString();

            }
        } catch (JacksonException ignored) {
        }

        throw new AnimeServiceException(
            "'" + request.url() + "' returns code " + response.code() + ": " + message
        );
    }

    @Override
    @NonNull
    public EpisodeInfo getEpisodeInfo() {
        return info;
    }

    @Override
    @NonNull
    public List<ServerInfo> getServers() {
        return Collections.unmodifiableList(servers);
    }

    @Override
    @NonNull
    public Optional<ServerInfo> getReadyServer() {
        return Optional.of(readyServer);
    }

    @Override
    @NonNull
    public Server fetchServer(ServerInfo info) throws IOException, AnimeServiceException {
        log.info("Fetching server: {}", info);

        ApiResponse response = fetchEpisodeData(info.getId());
        Provider provider = response.providers.stream()
                .filter(p -> p.id.equals(info.getId()))
                .findFirst()
                .get();

        return ApiResponseParser.createServer(info, provider);
    }
}
