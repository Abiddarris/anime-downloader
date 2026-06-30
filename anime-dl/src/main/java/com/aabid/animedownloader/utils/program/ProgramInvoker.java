package com.aabid.animedownloader.utils.program;

import java.io.IOException;
import java.nio.file.Path;

public interface ProgramInvoker {

    default Program invoke(
        StreamConsumer output, StreamConsumer error, String... args
    ) throws IOException {
        return invoke(Path.of("."), output, error, args);
    }

    Program invoke(
        Path workingDirectory, StreamConsumer output, StreamConsumer error, String... args
    ) throws IOException;
}
