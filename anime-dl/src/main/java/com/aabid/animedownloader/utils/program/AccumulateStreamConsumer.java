package com.aabid.animedownloader.utils.program;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AccumulateStreamConsumer implements StreamConsumer {

    private volatile ByteArrayOutputStream data = new ByteArrayOutputStream();

    @Override
    public void consume(InputStream stream) throws IOException {
        stream.transferTo(data);
    }

    public byte[] getBytes() {
        return data.toByteArray();
    }

}
