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

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.Quality;
import com.aabid.animedownloader.anime.ServerInfo;

class Selection {

    private @NonNull final ServerInfo serverInfo;
    private @NonNull final Quality quality;

    public Selection(@NonNull ServerInfo serverInfo, @NonNull Quality quality) {
        this.serverInfo = serverInfo;
        this.quality = quality;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public Quality getQuality() {
        return quality;
    }

}
