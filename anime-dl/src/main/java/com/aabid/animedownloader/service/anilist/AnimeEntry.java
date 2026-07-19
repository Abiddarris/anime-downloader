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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AnimeEntry {

    private int id;

    @NonNull
    private final String romajiTitle;

    @NonNull
    private final String englishTitle;

    @NonNull
    private final String format;

    @Nullable
    private final Integer episodeCount;

    public AnimeEntry(int id, @NonNull String romajiTitle, @NonNull String englishTitle, @NonNull String format,
            @Nullable Integer episodeCount) {
        this.id = id;
        this.romajiTitle = romajiTitle;
        this.englishTitle = englishTitle;
        this.format = format;
        this.episodeCount = episodeCount;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getRomajiTitle() {
        return romajiTitle;
    }

    @NonNull
    public String getEnglishTitle() {
        return englishTitle;
    }

    @NonNull
    public String getFormat() {
        return format;
    }

    @Nullable
    public Integer getEpisodeCount() {
        return episodeCount;
    }

}
