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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.service.ytdlp.Progress.State;
import com.aabid.animedownloader.utils.program.StreamConsumer;

class ProgressParserStreamConsumer implements StreamConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProgressParserStreamConsumer.class);

    private static final Pattern PROGRESS_PATTERN = Pattern.compile(
        "Progress: (?<status>.+)\\|\\|(?<downloaded>.+)\\|\\|(?<totalEstimate>.+)\\|\\|" +
        "(?<total>.+)\\|\\|(?<speed>.+)\\|\\|(?<fragmentIndex>.+)\\|\\|(?<fragmentCount>.+)"
    );

    private @NonNull final ProgressListener listener;

    public ProgressParserStreamConsumer(@NonNull ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void consume(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String data;
        Progress progress = null;
        while ((data = reader.readLine()) != null) {
            log.debug("Yt-dlp output: {}", data);

            Matcher matcher = PROGRESS_PATTERN.matcher(data);
            if (!matcher.find()) {
                continue;
            }

            String status = matcher.group("status");
            Long downloaded = parseAsLong(matcher, "downloaded");
            if (downloaded == null) {
                throw new IllegalArgumentException("downloaded section should not be null");
            }

            Long totalEstimate = parseAsLong(matcher, "totalEstimate");
            Long total = parseAsLong(matcher, "total");
            Long speed = parseAsLong(matcher, "speed");

            Long fragmentIndex = parseAsLong(matcher, "fragmentIndex");
            if (fragmentIndex == null && progress != null) {
                fragmentIndex = progress.getFragmentIndex();
            }

            Long fragmentCount = parseAsLong(matcher, "fragmentCount");
            if (fragmentCount == null && progress != null) {
                fragmentCount = progress.getFragmentCount();
            }

            progress = new Progress(
                State.valueOf(status.toUpperCase()), downloaded,
                totalEstimate, total, speed, fragmentCount, fragmentIndex
            );

            listener.onProgressUpdate(progress);
        }

    }

    @Nullable
    private Long parseAsLong(Matcher matcher, String name) {
        String value = matcher.group(name);
        if (value.equals("NA")) {
            return null;
        }

        Double valueDouble = Double.parseDouble(value);
        return Math.round(valueDouble);
    }

}
