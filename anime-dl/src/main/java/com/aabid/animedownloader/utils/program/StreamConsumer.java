package com.aabid.animedownloader.utils.program;

import java.io.IOException;
import java.io.InputStream;

public interface StreamConsumer {
    void consume(InputStream stream) throws IOException;
}
