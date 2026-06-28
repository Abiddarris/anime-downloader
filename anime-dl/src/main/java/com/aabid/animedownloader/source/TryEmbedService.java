package com.aabid.animedownloader.source;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.net.UserAgentProvider;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.java.net.cookiejar.JavaNetCookieJar;
import tools.jackson.databind.ObjectMapper;

public class TryEmbedService implements AnimeService {

    private static final Logger log = LoggerFactory.getLogger(TryEmbedService.class);

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @NonNull
    private final UserAgentProvider userAgentProvider;

    public TryEmbedService(OkHttpClient client, ObjectMapper mapper, @NonNull UserAgentProvider userAgentProvider) {
        this.client = client.newBuilder()
            .followRedirects(false)
            .build();
        this.mapper = mapper;
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public Episode queryEpisode(int animeId, int episode) throws IOException, AnimeNotFoundException {
        log.info("Querying anime source for animeId: {} (Episode {})", animeId, episode);

        CookieManager cookieHandler = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieJar cookieJar = new JavaNetCookieJar(cookieHandler);

        OkHttpClient client = this.client.newBuilder()
                .cookieJar(cookieJar)
                .build();

        return new TryEmbedEpisode(client, mapper, userAgentProvider, animeId, episode);
    }

}
