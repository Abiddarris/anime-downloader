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
