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
package com.aabid.animedownloader.service.animedl;

import java.io.IOException;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.anime.EpisodeInfo;
import com.aabid.animedownloader.service.anilist.AnilistService;
import com.aabid.animedownloader.service.anilist.AnimeMetadata;

/**
 * AniList-backed values provider for output filename template expansion.
 */
public class AnilistValuesProvider extends DefaultValuesProvider {

    private static Logger log = LoggerFactory.getLogger(AnilistValuesProvider.class);

    @NonNull
    private final AnilistService anilistService;

    public AnilistValuesProvider(@NonNull AnilistService anilistService) {
        this.anilistService = anilistService;
    }

    @Override
    @NonNull
    public Map<String, Object> getValues(@NonNull EpisodeInfo info, @NonNull Selection selection) {
        Map<String, Object> values = super.getValues(info, selection);
        try {
            AnimeMetadata metadata = anilistService.getMetadata(info.getAnilistId());
            values.put("anime_title", metadata.getRomajiTitle());
            values.put("english_anime_title", metadata.getEnglishTitle());
            values.put("native_anime_title", metadata.getNativeTitle());
        } catch (IOException e) {
            log.warn("Failed to fetch anime metadata from anilist: {}", e.getMessage());
            log.debug("Failed to fetch anime metadata from anilist", e);
        }

        return values;
    }
}
