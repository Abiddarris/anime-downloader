package com.aabid.animedownloader.m3u8;

import java.io.IOException;
import java.io.OutputStream;

public interface M3U8Downloader {
    void download(String url, String dest, OutputStream progressConsumer, OutputStream errorConsumer) throws IOException;
}
