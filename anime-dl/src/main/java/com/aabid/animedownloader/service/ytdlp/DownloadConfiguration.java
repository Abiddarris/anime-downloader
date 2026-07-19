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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadConfiguration {

    private final int buffersize;
    private final int concurrentFragment;
    private final boolean downloadSubtitle;
    private final boolean downloadThumbnail;
    private final Path temp;
    private final List<String> headers;
    private final Retries retries;

    private DownloadConfiguration(
            int buffersize, int concurrentFragment, boolean downloadSubtitle,
            boolean downloadThumbnail, Path temp, List<String> headers, Retries retries
    ) {
        this.buffersize = buffersize;
        this.concurrentFragment = concurrentFragment;
        this.downloadSubtitle = downloadSubtitle;
        this.downloadThumbnail = downloadThumbnail;
        this.temp = temp;
        this.headers = new ArrayList<>(headers);
        this.retries = retries;
    }

    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public Retries getFragmentRetries() {
        return retries;
    }

    public Path getTemp() {
        return temp;
    }

    public int getBuffersize() {
        return buffersize;
    }

    public int getConcurrentFragment() {
        return concurrentFragment;
    }

    public boolean isDownloadSubtitle() {
        return downloadSubtitle;
    }

    public boolean isDownloadThumbnail() {
        return downloadThumbnail;
    }

    public static class Builder {

        private int buffersize = 1024 * 16;
        private int concurrentFragment = 8;
        private boolean downloadSubtitle;
        private boolean downloadThumbnail;
        private Path temp;
        private List<String> headers = new ArrayList<>();
        private Retries retries = Retries.of(10);

        public Builder setTemp(Path temp) {
            this.temp = temp;
            return this;
        }

        public Builder setBuffersize(int buffersize) {
            if (buffersize <= 0) {
                throw new IllegalArgumentException("bufferSize can not be <= 0");
            }
            this.buffersize = buffersize;
            return this;
        }

        public Builder setConcurrentFragment(int concurrentFragment) {
            if (buffersize <= 0) {
                throw new IllegalArgumentException("concurrentFragment can not be <= 0");
            }

            this.concurrentFragment = concurrentFragment;
            return this;
        }

        public Builder setDownloadSubtitle(boolean downloadSubtitle) {
            this.downloadSubtitle = downloadSubtitle;
            return this;
        }

        public Builder setDownloadThumbnail(boolean downloadThumbnail) {
            this.downloadThumbnail = downloadThumbnail;
            return this;
        }

        public Builder setHeaders(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setFragmentRetries(Retries retries) {
            this.retries = retries;
            return this;
        }

        public DownloadConfiguration build() {
            return new DownloadConfiguration(
                buffersize, concurrentFragment, downloadSubtitle,
                downloadThumbnail, temp, headers, retries
            );
        }
    }
}
