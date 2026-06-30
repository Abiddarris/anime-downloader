package com.aabid.animedownloader.utils.program;

import java.io.IOException;
import java.nio.file.Path;

public interface ProgramInvoker {
    <T extends StreamConsumer> Program<T> invoke(
        Path workingDirectory, String[] args, StreamConsumerFactory<T> factory
    ) throws IOException;
}
