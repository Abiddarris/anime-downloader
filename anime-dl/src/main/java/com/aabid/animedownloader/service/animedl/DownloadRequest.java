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

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.utils.format.NewFormatter;

/**
 * Request object for downloading anime episodes.
 * Encapsulates all parameters needed for the download process.
 */
public class DownloadRequest {

    private final int episodeId;
    private final int animeId;
    private final @NonNull StreamSelector streamSelector;
    private final NewFormatter formatter;
    private final boolean simulate;
    private final boolean overwrite;

    private DownloadRequest(Builder builder) {
        this.episodeId = builder.episodeId;
        this.animeId = builder.animeId;
        this.streamSelector = builder.streamSelector;
        this.formatter = builder.formatter;
        this.simulate = builder.simulate;
        this.overwrite = builder.overwrite;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    @NonNull
    public StreamSelector getStreamSelector() {
        return streamSelector;
    }

    public int getAnimeId() {
        return animeId;
    }

    public NewFormatter getFormatter() {
        return formatter;
    }

    public boolean isSimulate() {
        return simulate;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    /**
     * Builder for DownloadRequest objects.
     */
    public static class Builder {

        private int episodeId;
        private int animeId;

        @SuppressWarnings("null")
        private @NonNull StreamSelector streamSelector =
            new SpecBasedStreamSelector(List.of(ServerSpec.ANY), QualitySpec.ANY);

        private NewFormatter formatter;
        private boolean simulate;
        private boolean overwrite = true;

        public Builder setEpisodeId(int episodeId) {
            this.episodeId = episodeId;
            return this;
        }

        public Builder setAnimeId(int animeId) {
            this.animeId = animeId;
            return this;
        }

        public Builder setStreamSelector(@NonNull StreamSelector streamSelector) {
            Objects.requireNonNull(streamSelector, "specs can not be null");

            this.streamSelector = streamSelector;
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

        public Builder setOverwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public DownloadRequest build() {
            return new DownloadRequest(this);
        }
    }
}