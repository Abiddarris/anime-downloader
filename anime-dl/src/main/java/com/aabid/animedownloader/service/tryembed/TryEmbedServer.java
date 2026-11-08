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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.anime.Caption;
import com.aabid.animedownloader.anime.Server;
import com.aabid.animedownloader.anime.ServerInfo;
import com.aabid.animedownloader.anime.StreamType;

class TryEmbedServer extends Server {

    private final @NonNull List<@NonNull TryEmbedQuality> qualities;
    private final @NonNull StreamType type;

    TryEmbedServer(@NonNull ServerInfo info, @NonNull List<@NonNull TryEmbedQuality> qualities,
                    @NonNull StreamType type, @NonNull List<@NonNull Caption> captions) {
        super(info, captions);

        this.qualities = new ArrayList<>(qualities);
        this.type = type;
    }

    @NonNull
    public List<@NonNull TryEmbedQuality> getQualities() {
        return Collections.unmodifiableList(qualities);
    }

    @NonNull
    public StreamType getType() {
        return type;
    }
}
