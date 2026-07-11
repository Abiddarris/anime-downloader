package com.aabid.animedownloader.service.anilist;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class SearchResponse {

    public Data data;

    static class Data {
        public Page Page;
    }

    static class Page {
        public List<Media> media;
    }

    static class Media {
        public int id;
        public String format;
        public Title title;
        public Integer episodes;
    }

    static class Title {
        public String romaji;
        public String english;

        @JsonProperty("native")
        public String nativeTitle;
    }
}
