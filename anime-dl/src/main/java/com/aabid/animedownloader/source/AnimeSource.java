package com.aabid.animedownloader.source;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

public class AnimeSource {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0";
    private static final String HOST = "https://tryembed.us.cc";

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public AnimeSource(OkHttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public Episode queryAnime(int animeId, int episode) throws IOException {
        String link = String.format(HOST + "/embed/anime/%d/%d/sub", animeId, episode);
        refreshCookie(link);
        return fetchEpisode(animeId, episode, link);
    }

    private Episode fetchEpisode(int animeId, int episodeNumber, String referer) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(HOST + "/api/stream_data?id=%d&episode=%d&audio=sub", animeId, episodeNumber))
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", referer)
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkSuccessful(request, response);

            Episode episode = mapper.readValue(response.body().string(), Episode.class);
            episode.setSourceLink(referer);
            return episode;
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

    private void checkSuccessful(Request request, Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(
                "'" + request.url() + "' returns code " + response.code() + ": " + response.body().string());
        }
    }

}
