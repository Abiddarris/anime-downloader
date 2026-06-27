package com.aabid.animedownloader.source;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

class EpisodeContext {

    private static final String HOST = "https://tryembed.us.cc";
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:152.0) Gecko/20100101 Firefox/152.0";
    private static final Logger log = LoggerFactory.getLogger(EpisodeContext.class);

    @NonNull
    private final OkHttpClient client;

    @NonNull
    private final ObjectMapper mapper;

    @Nullable
    private String nonce;

    @Nullable
    private String source;

    private int animeId;
    private int episode;

	EpisodeContext(@NotNull OkHttpClient client, @NonNull ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
	}

    @NonNull
    String getSource() {
        return source;
    }

    @NonNull
    OkHttpClient getClient() {
        return client;
    }

    @NonNull
    ApiResponse init(int animeId, int episode) throws IOException, AnimeNotFoundException {
        this.animeId = animeId;
        this.episode = episode;
        this.source = TryembedUrls.getEpisodeUrl(animeId, episode).toString();

        fetchCookiesAndRootNonce();
        return fetchEpisodeData(null);
    }

    private void fetchCookiesAndRootNonce() throws IOException {
        Request request = new Request.Builder()
                .url(source)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        log.debug("Fetching cookies and root nonce from: {}", source);
        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            String page = response.body().string();
            int nonceStatementIndex = page.indexOf("window.EMBED_NONCE=\"");
            int nonceIndex = nonceStatementIndex + "window.EMBED_NONCE=\"".length();;
            this.nonce = page.substring(nonceIndex, page.indexOf('"', nonceIndex));

            log.debug("Successfully updated cookies and root nonce.");
        }
    }

    @NonNull
    ApiResponse fetchEpisodeData(@Nullable String server) throws IOException {
        log.debug(
            "Fetching episode stream metadata - animeId: {}, episode: {}, server: {}, referer: {} nonce: {}",
            animeId, episode, server, source, nonce
        );

        Request request = new Request.Builder()
                .url(TryembedUrls.getEpisodeApiUrl(animeId, episode, server, nonce))
                .addHeader("User-Agent", USER_AGENT)
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

    private void checkSuccessful(Request request, Response response) throws IOException {
        if (response.isRedirect()) {
            return;
        }

        if (!response.isSuccessful()) {
            throw new IOException(
                    "'" + request.url() + "' returns code " + response.code() + ": " + response.body().string());
        }
    }
}
