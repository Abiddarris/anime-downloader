package com.aabid.animedownloader.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:152.0) Gecko/20100101 Firefox/152.0";
    private static final String HOST = "https://tryembed.us.cc";
    private static final String HOST_NAME = "tryembed.us.cc";
    private static final Logger log = LoggerFactory.getLogger(AnimeSource.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final Map<Metadata, String> nonces = new HashMap<>();

    public AnimeSource(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public Episode queryAnime(int animeId, int episode) throws IOException, AnimeNotFoundException {
        log.info("Querying anime source for animeId: {} (Episode {})", animeId, episode);

        String link = String.format(HOST + "/embed/anime/%d/%d/sub", animeId, episode);
        String nonce = getCookiesAndRootNonce(link);

        ApiResponse response = fetchEpisodeData(animeId, episode, null, link, nonce);
        Episode eps = ApiResponseParser.createEpisode(response, link);

        nonces.put(eps.getMetadata(), response.embedNonce);

        return eps;
    }

    @NonNull
    private String getCookiesAndRootNonce(@NonNull String link) throws IOException {
        Request request = new Request.Builder()
                .url(link)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        log.debug("Fetching cookies and root nonce from: {}", link);
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String page = response.body().string();
            int nonceStatementIndex = page.indexOf("window.EMBED_NONCE=\"");
            int nonceIndex = nonceStatementIndex + "window.EMBED_NONCE=\"".length();;
            String nonce = page.substring(nonceIndex, page.indexOf('"', nonceIndex));

            log.debug("Successfully updated cookies and root nonce.");
            return nonce;
        }
    }

    @Nullable
    private ApiResponse fetchEpisodeData(
            int animeId, int episodeNumber, @Nullable String server,
            @NonNull String referer, @NonNull String nonce) throws IOException {
        log.debug(
            "Fetching episode stream metadata - animeId: {}, episode: {}, server: {}, referer: {} nonce: {}",
            animeId, episodeNumber, server, referer, nonce);

        Request request = new Request.Builder()
                .url(buildFetchEpisodeAPIUrl(animeId, episodeNumber, server, nonce))
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("X-Embed-Nonce", nonce)
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
    private HttpUrl buildFetchEpisodeAPIUrl(
            int animeId, int episodeNumber, @Nullable String server, @NonNull String nonce) {
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

        builder.addQueryParameter("nonce", nonce);

        return builder.build();
    }

    public void fetchServer(Server server) throws IOException {
        log.info("Fetching server: {}", server);

        Metadata metadata = server.getMetadata();
        ApiResponse response = fetchEpisodeData(
            metadata.getAnilistId(), metadata.getEpisode(), server.getId(), metadata.getSource(), nonces.get(metadata)
        );
        nonces.put(metadata, response.embedNonce);

        Provider provider = response.providers.stream()
            .filter(p -> p.id.equals(server.getId()))
            .findFirst()
            .get();
        if (!provider.status.equals("ready")) {
            throw new IOException("server is not ready");
        }

        server.setQualities(ApiResponseParser.createQualities(provider.qualities, metadata));
    }

    public void resolveQuality(Quality quality) throws IOException {
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
                .header("Referer", quality.getMetadata().getSource())
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
