package com.aabid.animedownloader.source;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.source.ApiResponse.Provider;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

public class AnimeSource {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0";
    private static final String HOST = "https://tryembed.us.cc";
    private static final String HOST_NAME = "tryembed.us.cc";
    private static final Logger log = LoggerFactory.getLogger(AnimeSource.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public AnimeSource(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public Episode queryAnime(int animeId, int episode) throws IOException {
        log.info("Querying anime source for animeId: {} (Episode {})", animeId, episode);

        String link = String.format(HOST + "/embed/anime/%d/%d/sub", animeId, episode);
        getEssentialCookies(link);

        ApiResponse response = fetchEpisodeData(animeId, episode, null, link);
        return ApiResponseParser.parseResponse(response, link);
    }

    private void getEssentialCookies(@NonNull String link) throws IOException {
        Request request = new Request.Builder()
                .url(link)
                .build();

        log.debug("Fetching essential session cookies from: {}", link);
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);
            log.debug("Successfully updated session cookies.");
        }
    }

    @Nullable
    private ApiResponse fetchEpisodeData(
            int animeId, int episodeNumber, @Nullable String server, @NonNull String referer) throws IOException {
        log.debug("Fetching episode stream metadata - animeId: {}, episode: {}, server: {}, referer: {}",
                animeId, episodeNumber, server, referer);

        Request request = new Request.Builder()
                .url(buildFetchEpisodeAPIUrl(animeId, episodeNumber, server))
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", referer)
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .build();

        log.debug("Executing API request to: {}", request.url());
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String apiResponse = response.body().string();
            log.debug("Received raw JSON response from API: {}", apiResponse);

            return mapper.readValue(apiResponse, ApiResponse.class);
        }
    }

    @NonNull
    private HttpUrl buildFetchEpisodeAPIUrl(int animeId, int episodeNumber, @Nullable String server) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host(HOST_NAME)
                .addPathSegment("api")
                .addPathSegment("stream_data")
                .addQueryParameter("id", String.valueOf(animeId))
                .addQueryParameter("episode", String.valueOf(episodeNumber))
                .addQueryParameter("audio", "sub");

        if (server != null) {
            builder.addQueryParameter("server", server);
        }
        return builder.build();
    }

    public void fetchServer(Server server) throws IOException {
        log.info("Fetching server: {}", server);

        Metadata metadata = server.getMetadata();
        ApiResponse response = fetchEpisodeData(
            metadata.getAnilistId(), metadata.getEpisode(), server.getId(), metadata.getSource()
        );
        Provider provider = response.providers.stream()
            .filter(p -> p.id.equals(server.getId()))
            .findFirst()
            .get();
        if (!provider.status.equals("ready")) {
            throw new IOException("server is not ready");
        }

        server.setQualities(ApiResponseParser.createQualities(provider.qualities));
    }

    public void resolveQuality(Quality quality, String referer) throws IOException {
        if (quality.isResolved()) {
            return;
        }

        log.info("Resolving direct target file path for quality: {}", quality.getName());

        TokenBasedQuality tokenBasedQuality = (TokenBasedQuality)quality;
        Request request = new Request.Builder()
                .url(resolveTokenUrl(tokenBasedQuality.getToken()))
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0")
                .header("Accept", "*/*")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", referer)
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin")
                .header("TE", "trailers")
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String realLink = response.header("Location");
            log.debug("Resolved direct mirror stream link: {}", realLink);

            tokenBasedQuality.resolve(realLink);
        }

    }

    private HttpUrl resolveTokenUrl(String token) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(HOST_NAME)
                .addPathSegment("s")
                .addPathSegment(token + ".m3u8")
                .build();
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
