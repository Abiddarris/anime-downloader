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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.AnimeServiceException;
import com.aabid.animedownloader.anime.Caption;
import com.aabid.animedownloader.anime.Episode;
import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.anime.StreamType;
import com.aabid.animedownloader.net.UserAgentProvider;
import com.aabid.animedownloader.service.tryembed.ApiResponse.Provider;

import io.lindstrom.m3u8.model.MultivariantPlaylist;
import io.lindstrom.m3u8.model.Variant;
import io.lindstrom.m3u8.parser.MultivariantPlaylistParser;
import io.lindstrom.m3u8.parser.ParsingMode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        this.nonceManager = new NonceManager();

        String bootstrapTicket = fetchCookiesAndBootstrapTicket();
        ApiResponse response = bootstrap(bootstrapTicket);

        this.info = ApiResponseParser.createEpisodeInfo(response);
        this.servers = ApiResponseParser.createServers(response.providers);
        this.readyServer = ApiResponseParser.getReadyServer(this.servers, response);
	}

    private String fetchCookiesAndBootstrapTicket() throws IOException, AnimeServiceException {
        Request request = new Request.Builder()
                .url(source)
                .addHeader("User-Agent", userAgentProvider.getUserAgent())
                .build();

        log.debug("Fetching cookies and bootstrap ticket from: {}", source);

        return executeRequest(request, response -> {
            String page = response.body().string();
            int bootstrapTicketIndex = page.indexOf("window.BOOTSTRAP_TICKET=\"");
            int ticketIndex = bootstrapTicketIndex + "window.BOOTSTRAP_TICKET=\"".length();

            String bootstrapTicket = page.substring(ticketIndex, page.indexOf('"', ticketIndex));

            log.debug("Successfully updated cookies and bootstrap ticket.");
            return bootstrapTicket;
        });
    }

    @NonNull
    private ApiResponse bootstrap(@NonNull String ticket) throws IOException, AnimeServiceException {
        log.debug("Initializing bootstrap process with ticket: {}", ticket);

        Request request = new Request.Builder()
                .post(RequestBody.create(new byte[0]))
                .url(TryembedUrls.getBootstrapUrl())
                .addHeader("User-Agent", userAgentProvider.getUserAgent())
                .addHeader("X-TryEmbed-Bootstrap", ticket)
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

            String bootstrapResponseStr = body.string();
            log.debug("Received raw JSON response from bootstrap: {}", bootstrapResponseStr);

            try {
                ApiResponse apiResponse = mapper.readValue(bootstrapResponseStr, ApiResponse.class);
                if (apiResponse == null) {
                    throw new AnimeServiceException("Failed to parse bootstrap response; deserialized object is null");
                }
                nonceManager.update(apiResponse.embedNonce);

                return apiResponse;
            } catch (JacksonException e) {
                throw new AnimeServiceException("Failed to deserialize bootstrap response JSON", e);
            }
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
    public @NonNull List<@NonNull Quality> resolveQualities(Server server) throws IOException, AnimeServiceException {
        if (!(server instanceof TryEmbedServer)) {
            throw new IllegalArgumentException("Server must be an instance of TryEmbedServer");
        }

        List<@NonNull Quality> qualities = new ArrayList<>();
        TryEmbedServer tryEmbedServer = (TryEmbedServer) server;

        if (tryEmbedServer.getType() == StreamType.UNKNOWN) {
            log.warn("Unsupported stream type for server {}", tryEmbedServer.getInfo().getName());
            return qualities;
        }

        if (tryEmbedServer.getType() == StreamType.MP4) {
            for (TryEmbedQuality quality : tryEmbedServer.getQualities()) {
                try {
                    String directLink = resolveDirectLink(quality);
                    qualities.add(new Quality(
                        quality.getName(), tryEmbedServer.getType(),
                        parseResolutionFromName(quality.getName()), directLink
                    ));
                } catch (IOException | AnimeServiceException e) {
                    log.error("Failed to resolve direct link for quality: {}", quality.getName(), e);
                }
            }
            return qualities;
        }

        for (TryEmbedQuality quality : tryEmbedServer.getQualities()) {
            try {
                String directLink = resolveDirectLink(quality);
                Request request = new Request.Builder()
                    .url(directLink)
                    .header("User-Agent", userAgentProvider.getUserAgent())
                    .header("Accept", "*/*")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Referer", source)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("TE", "trailers")
                    .build();

                String m3u8 = executeRequest(request, response -> {
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new AnimeServiceException("response body is null.");
                    }

                    return body.string();
                });

                if (!isMasterPlaylist(m3u8)) {
                    qualities.add(new Quality(
                        quality.getName(), tryEmbedServer.getType(),
                        parseResolutionFromName(quality.getName()), directLink
                    ));
                    continue;
                }

                MultivariantPlaylistParser masterParser = new MultivariantPlaylistParser(ParsingMode.LENIENT);
                MultivariantPlaylist master = masterParser.readPlaylist(m3u8);

                for (Variant variant : master.variants()) {
                    qualities.add(new Quality(
                        variant.resolution().get().height() + "p",
                        tryEmbedServer.getType(),
                        variant.resolution().get().height(), TryembedUrls.resolveURL(variant.uri()).toString()
                    ));
                }
            } catch (IOException | AnimeServiceException e) {
                log.debug("Failed to resolve direct link for quality: {}", quality.getName(), e);
            }
        }
        return qualities;
    }

    private static boolean isMasterPlaylist(@NonNull String content) {
        return content.contains("#EXT-X-STREAM-INF");
    }

    private static int parseResolutionFromName(@NonNull String name) {
        if (name.endsWith("p")) {
            name = name.substring(0, name.length() - 1);
        }

        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse resolution from stream name {}", name);
            return -1;
        }
    }


    @NonNull
    private String resolveDirectLink(@NonNull TryEmbedQuality quality) throws IOException, AnimeServiceException {
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

    @Override
    public byte[] downloadCaption(Caption caption) throws IOException, AnimeServiceException {
        Request request = new Request.Builder()
                .url(TryembedUrls.resolveURL(caption.getUrl()))
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
            ResponseBody body = response.body();
            if (body == null) {
                throw new AnimeServiceException("response body is null.");
            }

            return body.bytes();
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
