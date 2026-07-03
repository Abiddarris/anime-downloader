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
