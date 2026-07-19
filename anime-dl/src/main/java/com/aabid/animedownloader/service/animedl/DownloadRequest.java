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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.utils.format.NewFormatter;
import com.google.common.collect.Lists;

/**
 * Request object for downloading anime episodes.
 * Encapsulates all parameters needed for the download process.
 */
public class DownloadRequest {

    private final int episodeId;
    private final int animeId;
    private final @NonNull List<ServerSpec> serverSpec;
    private final String qualityName;
    private final NewFormatter formatter;
    private final boolean simulate;

    private DownloadRequest(Builder builder) {
        this.episodeId = builder.episodeId;
        this.animeId = builder.animeId;
        this.serverSpec = builder.serverId;
        this.qualityName = builder.qualityName;
        this.formatter = builder.formatter;
        this.simulate = builder.simulate;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public int getAnimeId() {
        return animeId;
    }

    public @NonNull List<ServerSpec> getServerSpec() {
        return serverSpec;
    }

    public String getQualityName() {
        return qualityName;
    }

    public NewFormatter getFormatter() {
        return formatter;
    }

    public boolean isSimulate() {
        return simulate;
    }

    /**
     * Builder for DownloadRequest objects.
     */
    public static class Builder {

        private int episodeId;
        private int animeId;
        private @NonNull List<ServerSpec> serverId = Lists.newArrayList(ServerSpec.ANY);
        private String qualityName;
        private NewFormatter formatter;
        private boolean simulate;

        public Builder setEpisodeId(int episodeId) {
            this.episodeId = episodeId;
            return this;
        }

        public Builder setAnimeId(int animeId) {
            this.animeId = animeId;
            return this;
        }

        public Builder setServerId(@NonNull List<ServerSpec> specs) {
            Objects.requireNonNull(specs, "specs can not be null");

            this.serverId = new ArrayList<>(specs);
            if (this.serverId.isEmpty()) {
                this.serverId.add(ServerSpec.ANY);
                return this;
            }

            int lastIndex = this.serverId.size() - 1;
            if ((this.serverId.indexOf(ServerSpec.ANY) > 0 && this.serverId.indexOf(ServerSpec.ANY) != lastIndex) ||
                (this.serverId.indexOf(ServerSpec.NONE) > 0 && this.serverId.indexOf(ServerSpec.NONE) != lastIndex) ) {
                throw new IllegalArgumentException("ANY and NONE can only appear on the last element");
            }

            ServerSpec spec = this.serverId.get(lastIndex);
            if (!spec.equals(ServerSpec.ANY) && !spec.equals(ServerSpec.NONE)) {
                this.serverId.add(ServerSpec.NONE);
            }

            return this;
        }

        public Builder setQualityName(String qualityName) {
            this.qualityName = qualityName;
            return this;
        }

        public Builder setFormatter(NewFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public Builder setSimulate(boolean simulate) {
            this.simulate = simulate;
            return this;
        }

        public DownloadRequest build() {
            return new DownloadRequest(this);
        }
    }
}