package com.aabid.animedownloader.service.animedl;

import com.aabid.animedownloader.utils.format.NewFormatter;

/**
 * Request object for downloading anime episodes.
 * Encapsulates all parameters needed for the download process.
 */
public class DownloadRequest {
    private final int episodeId;
    private final int animeId;
    private final String serverId;
    private final String qualityName;
    private final NewFormatter formatter;
    private final boolean simulate;

    private DownloadRequest(Builder builder) {
        this.episodeId = builder.episodeId;
        this.animeId = builder.animeId;
        this.serverId = builder.serverId;
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

    public String getServerId() {
        return serverId;
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
        private String serverId;
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

        public Builder setServerId(String serverId) {
            this.serverId = serverId;
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