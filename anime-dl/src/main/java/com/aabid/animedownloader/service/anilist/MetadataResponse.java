package com.aabid.animedownloader.service.anilist;

import com.fasterxml.jackson.annotation.JsonProperty;

class MetadataResponse {

    public Data data;

    static class Data {
        public Media Media;
    }

    static class Media {
        public Title title;
    }

    static class Title {
        public String romaji;
        public String english;

        @JsonProperty("native")
        public String nativeTitle;
    }

}
