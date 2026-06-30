package com.aabid.animedownloader.utils.program;

public interface Program<T extends StreamConsumer> {
    T getOutputStreamConsumer();
    T getErrorStreamConsumer();
    int getExitCode() throws InterruptedException;
}