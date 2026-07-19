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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.utils.program.AccumulateStreamConsumer;
import com.aabid.animedownloader.utils.program.ArgumentBuilder;
import com.aabid.animedownloader.utils.program.Program;
import com.aabid.animedownloader.utils.program.ProgramInvoker;
import com.aabid.animedownloader.utils.program.StreamConsumer;

public class YtDlp {

    private static final Logger log = LoggerFactory.getLogger(YtDlp.class);

    private static final String PROGRESS_TEMPLATE = "Progress: %(progress.status)s||%(progress.downloaded_bytes)s||" +
        "%(progress.total_bytes_estimate)s||%(progress.total_bytes)s||%(progress.speed)s||" +
        "%(progress.fragment_index)s||%(progress.fragment_count)s";
    private final ProgramInvoker invoker;

    public YtDlp(@NonNull ProgramInvoker invoker) {
        Objects.requireNonNull(invoker, "invoker must not be null");

        this.invoker = invoker;
    }

    public void download(DownloadConfiguration configuration, String link, Path out, @Nullable ProgressListener listener)
            throws IOException, YtDlpInvocationException, InterruptedException, HttpException {
        Objects.requireNonNull(configuration, "configuration must not be null");
        Objects.requireNonNull(link, "link must not be null");
        Objects.requireNonNull(out, "out must not be null");

        if (listener == null) {
            listener = progress -> {};
        }

        ArgumentBuilder builder = new ArgumentBuilder()
            .addOption("-o", out.getFileName().toString())
            .addBooleanOptions("--abort-on-unavailable-fragments")
            .setPositionalArguments(link);

        applyConfiguration(configuration, builder);

        Path workingDirectory = out.getParent();
        StreamConsumer output = new ProgressParserStreamConsumer(listener);
        AccumulateStreamConsumer error = new AccumulateStreamConsumer();

        String[] args = builder.build();
        log.debug("Invoking yt-dlp with args: {}", Arrays.toString(args));

        Program program;
        if (workingDirectory == null) {
            program = invoker.invoke(output, error, args);
        } else {
            program = invoker.invoke(workingDirectory, output, error, args);
        }

        int exitCode = program.getExitCode();
        if (exitCode != 0) {
            YtDlpInvocationException e = new YtDlpInvocationException(exitCode, new String(error.getBytes()).trim());
            ExceptionTranslator.translate(e);
        }
    }

    private static void applyConfiguration(DownloadConfiguration configuration, ArgumentBuilder builder) {
        Retries retries = configuration.getFragmentRetries();
        builder.addOption("--buffer-size", String.valueOf(configuration.getBuffersize()))
            .addOption("--concurrent-fragments", String.valueOf(configuration.getConcurrentFragment()))
            .addOption("--fragment-retries",
                    retries == Retries.infinite() ? "infinite" : String.valueOf(retries.getRetries()))
            .addOption("--progress-template", PROGRESS_TEMPLATE)
            .addBooleanOptions("--newline");

        for (String header : configuration.getHeaders()) {
            builder.addOption("--add-headers", header);
        }

        if (configuration.isDownloadSubtitle()) {
            builder.addOption("--sub-langs", "all,-live_chat")
                .addBooleanOptions("--embed-subs");
        }

        if (configuration.isDownloadThumbnail()) {
            builder.addBooleanOptions("--embed-thumbnail");
        }

        if (configuration.getTemp() != null) {
            builder.addOption("-P", "temp:" + configuration.getTemp().toAbsolutePath().normalize());
        }
    }

}

