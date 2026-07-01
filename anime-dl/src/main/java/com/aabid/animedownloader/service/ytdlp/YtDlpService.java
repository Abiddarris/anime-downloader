package com.aabid.animedownloader.service.ytdlp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.aabid.animedownloader.utils.program.AccumulateStreamConsumer;
import com.aabid.animedownloader.utils.program.ArgumentBuilder;
import com.aabid.animedownloader.utils.program.Program;
import com.aabid.animedownloader.utils.program.ProgramInvoker;
import com.aabid.animedownloader.utils.program.StreamConsumer;

public class YtDlpService {

    private final ProgramInvoker invoker;

    public YtDlpService(@NonNull ProgramInvoker invoker) {
        Objects.requireNonNull(invoker, "invoker must not be null");

        this.invoker = invoker;
    }

    public void download(DownloadConfiguration configuration, String link, Path out)
            throws IOException, YtDlpInvocationException, InterruptedException {
        Objects.requireNonNull(configuration, "configuration must not be null");
        Objects.requireNonNull(link, "link must not be null");
        Objects.requireNonNull(out, "out must not be null");

        ArgumentBuilder builder = new ArgumentBuilder()
            .addOption("-o", out.getFileName().toString())
            .setPositionalArguments(link);

        applyConfiguration(configuration, builder);

        Path workingDirectory = out.getParent();
        StreamConsumer output = (outstream) -> outstream.transferTo(System.out);
        AccumulateStreamConsumer error = new AccumulateStreamConsumer();

        Program program;
        if (workingDirectory == null) {
            program = invoker.invoke(output, error, builder.build());
        } else {
            program = invoker.invoke(workingDirectory, output, error, builder.build());
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
                    retries == Retries.infinite() ? "infinite" : String.valueOf(retries.getRetries()));

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

