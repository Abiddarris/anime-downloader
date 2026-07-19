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
package com.aabid.animedownloader.service.anilist;


import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.service.anilist.SearchResponse.Media;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;

public class AnilistService {

    private static final Logger log = LoggerFactory.getLogger(AnilistService.class);

    private static final String ANILIST_GRAPHQL_URL = "https://graphql.anilist.co";
    private static final String ANILIST_QUERY = """
        query ($search: String, $page: Int, $perPage: Int) {
        Page(page: $page, perPage: $perPage) {
            media(search: $search, type: ANIME) {
            id
            title { romaji english native}
            format
            episodes
            }
        }
        }
        """;

    private static final int ITEM_PER_PAGE = 5;

    @NonNull
    private OkHttpClient client;

    @NonNull
    private ObjectMapper mapper;

    public AnilistService(@NonNull OkHttpClient client, @NonNull ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @NonNull
    public List<AnimeEntry> search(@NonNull String keyword, int page) throws IOException {
        GraphQLRequest graphQLRequest = new GraphQLRequest(ANILIST_QUERY);
        graphQLRequest.setVariable("search", keyword);
        graphQLRequest.setVariable("page", page);
        graphQLRequest.setVariable("perPage", ITEM_PER_PAGE);

        String response = executeGraphQLRequest(graphQLRequest);
        SearchResponse searchResponse = mapper.readValue(response, SearchResponse.class);
        return searchResponse.data.Page.media.stream()
            .map(this::newAnimeEntry)
            .toList();
    }

    @NonNull
    private String executeGraphQLRequest(GraphQLRequest graphQLRequest) throws IOException {
        String requestJson = mapper.writer()
            .withDefaultPrettyPrinter()
            .writeValueAsString(graphQLRequest);

        RequestBody body = RequestBody.create(requestJson, MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(ANILIST_GRAPHQL_URL)
            .post(body)
            .build();

        log.debug("Making request to {} with payload: {}", ANILIST_GRAPHQL_URL, requestJson);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            log.debug("Received GraphQL response (Status: {}): {}", response.code(), responseBody);

            return responseBody;
         }
    }

    @NonNull
    private AnimeEntry newAnimeEntry(Media media) {
        return new AnimeEntry(
            media.id, media.title.romaji,
            media.title.english == null ? "" : media.title.english,
            media.format, media.episodes
        );
    }

}
