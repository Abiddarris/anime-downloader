package com.aabid.animedownloader.source;

import java.util.List;

class ApiResponse {

    public List<Provider> providers;
    public Provider selectedProvider;
    public Meta meta;
    public String animeTitle;

    static class Provider {
        public String id;
        public String name;
        public String type;
        public String status;
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
}
