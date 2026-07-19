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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

public abstract class Episode {

    @NonNull
    public abstract EpisodeInfo getEpisodeInfo();

    @NonNull
    public abstract List<ServerInfo> getServers();

    @NonNull
    public abstract Optional<ServerInfo> getReadyServer();

    @NonNull
    public abstract Server fetchServer(ServerInfo info) throws IOException, AnimeServiceException;

    @NonNull
    public abstract String resolveQuality(Quality quality) throws IOException, AnimeServiceException;

    public final Optional<ServerInfo> findServerById(String id) {
        return getServers().stream()
                .filter(server -> server.getId().equals(id))
                .findFirst();
    }
}