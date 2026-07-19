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
package com.aabid.animedownloader.service.tryembed;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

class ApiResponse {

    public List<Provider> providers;
    public Provider selectedProvider;
    public Meta meta;
    public String animeTitle;
    public String embedNonce;
    public String posterUrl;
    public Mark intro;
    public Mark outro;

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

    static class Mark {
        public int start;
        public int end;
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