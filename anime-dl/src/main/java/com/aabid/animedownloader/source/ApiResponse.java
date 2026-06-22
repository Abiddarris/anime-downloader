package com.aabid.animedownloader.source;

import java.util.List;

class ApiResponse {

    public List<Provider> providers;
    public Provider selectedProvider;
    public Meta meta;

    static class Provider {
        public String id;
        public String name;
        public String type;
        public String status;
        public List<StreamQuality> qualities;
    }

    static class StreamQuality {
        public String name;
        public String token;
        public String fallbackToken;
    }

    static class Meta {
        public String type;
        public int anilist_id;
        public int episode;
        public String audio;
    }
}
