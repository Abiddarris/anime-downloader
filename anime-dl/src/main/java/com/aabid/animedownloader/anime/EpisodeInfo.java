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
package com.aabid.animedownloader.anime;

import org.jspecify.annotations.NonNull;

public class EpisodeInfo {

    private final int anilistId;
    private final int episode;

    @NonNull
    private final String title;

    public EpisodeInfo(int anilistId, int episode, @NonNull String title) {
        this.anilistId = anilistId;
        this.episode = episode;
        this.title = title;
    }

    public int getAnilistId() {
        return anilistId;
    }

    public int getEpisode() {
        return episode;
    }

    @NonNull
    public String getAnimeTitle() {
        return title;
    }

}
