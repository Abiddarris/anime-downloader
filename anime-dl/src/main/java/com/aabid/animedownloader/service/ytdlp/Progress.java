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
package com.aabid.animedownloader.service.ytdlp;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class Progress {

    @NonNull
    private final State state;
    private final long downloaded;

    @Nullable
    private final Long totalEstimate;

    @Nullable
    private final Long total;

    @Nullable
    private final Long speed;

    @Nullable
    private final Long fragmentCount;

    @Nullable
    private final Long fragmentIndex;

    public Progress(@NonNull State state, long downloaded, @Nullable Long totalEstimate, @Nullable Long total,
            @Nullable Long speed, @Nullable Long fragmentCount, @Nullable Long fragmentIndex) {
        this.state = state;
        this.downloaded = downloaded;
        this.totalEstimate = totalEstimate;
        this.total = total;
        this.speed = speed;
        this.fragmentCount = fragmentCount;
        this.fragmentIndex = fragmentIndex;
    }

    @NonNull
    public State getState() {
        return state;
    }

    public long getDownloaded() {
        return downloaded;
    }

    @Nullable
    public Long getTotalEstimate() {
        return totalEstimate;
    }

    @Nullable
    public Long getTotal() {
        return total;
    }

    @Nullable
    public Long getSpeed() {
        return speed;
    }

    @Nullable
    public Long getFragmentCount() {
        return fragmentCount;
    }

    @Nullable
    public Long getFragmentIndex() {
        return fragmentIndex;
    }

    public static enum State {
        DOWNLOADING, FINISHED
    }
}
