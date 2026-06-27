package com.aabid.animedownloader.source;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.source.Server.ServerState;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.java.net.cookiejar.JavaNetCookieJar;
import tools.jackson.databind.ObjectMapper;

public class AnimeService {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:152.0) Gecko/20100101 Firefox/152.0";
    private static final Logger log = LoggerFactory.getLogger(AnimeService.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public AnimeService(OkHttpClient client, ObjectMapper mapper) {
        this.client = client.newBuilder()
            .followRedirects(false)
            .build();
        this.mapper = mapper;
    }

    public Episode queryAnime(int animeId, int episode) throws IOException, AnimeNotFoundException {
        log.info("Querying anime source for animeId: {} (Episode {})", animeId, episode);

        CookieManager cookieHandler = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieJar cookieJar = new JavaNetCookieJar(cookieHandler);

        OkHttpClient client = this.client.newBuilder()
                .cookieJar(cookieJar)
                .build();

        EpisodeContext context = new EpisodeContext(client, mapper);
        ApiResponse response = context.init(animeId, episode);

        return ApiResponseParser.createEpisode(response, context);
    }

    public void fetchServer(Server server) throws IOException {
        log.info("Fetching server: {}", server);

        ApiResponse response = server.getContext().fetchEpisodeData(server.getId());
        ApiResponseParser.updateServerStatusFromResponse(server, response);

        if (server.getState() == ServerState.FAILED) {
            throw new IOException("server is not ready");
        }
    }

    public void resolveQuality(Quality quality) throws IOException {
        if (quality.isResolved()) {
            return;
        }

        log.info("Resolving direct target file path for quality: {}", quality.getName());

        TokenBasedQuality tokenBasedQuality = (TokenBasedQuality)quality;
        EpisodeContext context = tokenBasedQuality.getContext();
        Request request = new Request.Builder()
                .url(TryembedUrls.getTokenResolutionUrl(tokenBasedQuality.getToken()))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "*/*")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", context.getSource())
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("TE", "trailers")
                .build();

        try (Response response = context.getClient().newCall(request).execute()) {
            checkSuccessful(request, response);

            String realLink = response.header("Location");
            log.debug("Resolved direct mirror stream link: {}", realLink);

            tokenBasedQuality.resolve(realLink);
        }

    }

    private void checkSuccessful(Request request, Response response) throws IOException {
        if (response.isRedirect()) {
            return;
        }

        if (!response.isSuccessful()) {
            throw new IOException(
                "'" + request.url() + "' returns code " + response.code() + ": " + response.body().string()
            );
        }
    }

}
