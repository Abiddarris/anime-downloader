package com.aabid.animedownloader.utils.program;

public interface StreamConsumerFactory<T extends StreamConsumer> {
    T newStreamConsumer();
}
