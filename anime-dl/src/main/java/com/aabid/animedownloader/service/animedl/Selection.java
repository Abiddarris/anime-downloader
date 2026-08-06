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
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;

class Selection {

    private @NonNull final Server server;
    private @NonNull final Quality quality;

    public Selection(@NonNull Server server, @NonNull Quality quality) {
        this.server = server;
        this.quality = quality;
    }

    @NonNull
    public ServerInfo getServerInfo() {
        return server.getInfo();
    }

    @NonNull
    public Server getServer() {
        return server;
    }

    @NonNull
    public Quality getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return "Selection [serverInfo=" + server + ", quality=" + quality + "]";
    }

}
