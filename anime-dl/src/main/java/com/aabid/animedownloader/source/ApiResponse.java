package com.aabid.animedownloader.source;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class ApiResponse {

    public List<Provider> providers;
    public Provider selectedProvider;
    public Meta meta;
    public String animeTitle;
    public String embedNonce;

    static class Provider {
        public String id;
        public String name;
        public String type;
        public Status status;
        public List<StreamQuality> qualities;
    }

    static class StreamQuality {
        public String name;

        // token based stream
        public String token;
        public String fallbackToken;

        // direct stream
        public String directUrl;
        public boolean isM3U8;
    }

    static class Meta {
        public String type;
        public int anilist_id;
        public int episode;
        public String audio;
    }

    static enum Status {
        @JsonProperty("idle")
        IDLE,

        @JsonProperty("failed")
        FAILED,

        @JsonProperty("ready")
        READY
    }
}