package com.aabid.animedownloader.source;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.aabid.animedownloader.source.ApiResponse.Provider;
import com.aabid.animedownloader.source.ApiResponse.StreamQuality;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

public class AnimeSource {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0";
    private static final String HOST = "https://tryembed.us.cc";
    private static final String HOST_NAME = "tryembed.us.cc";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public AnimeSource(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public Episode queryAnime(int animeId, int episode) throws IOException {
        String link = String.format(HOST + "/embed/anime/%d/%d/sub", animeId, episode);
        refreshCookie(link);

        ApiResponse response = fetchEpisodeData(animeId, episode, null, link);
        return ApiResponseParser.parseResponse(response, link);
    }

    public void fetchServer(Server server) throws IOException {
        Episode episode = server.getEpisode();
        ApiResponse response = fetchEpisodeData(
            episode.getId(), episode.getEpisode(), server.getId(), episode.getSourceLink()
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
        TokenBasedQuality tokenBasedQuality = (TokenBasedQuality)quality;
        String streamLink = "https://tryembed.us.cc/s/" + tokenBasedQuality.getToken() + ".m3u8";
        Request request = new Request.Builder()
                .url(streamLink)
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

            tokenBasedQuality.resolve(response.header("Location"));
        }

    }

    private void refreshCookie(String link) throws IOException {
        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);
        }
    }

    private @Nullable ApiResponse fetchEpisodeData(
            int animeId, int episodeNumber, @Nullable String server, @NonNull String referer) throws IOException {
        Request request = new Request.Builder()
                .url(buildUrl(animeId, episodeNumber, server))
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", referer)
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            return mapper.readValue(response.body().string(), ApiResponse.class);
        }
    }

    @NonNull
    private HttpUrl buildUrl(int animeId, int episodeNumber, @Nullable String server) {
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
