package com.aabid.animedownloader.m3u8;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

public class YtDlpM3U8Downloader implements M3U8Downloader {

    private ExecutorService service;

    public YtDlpM3U8Downloader(ExecutorService service) {
        this.service = service;
    }

    @Override
    public void download(String url, String dest,
                        OutputStream progressConsumer, OutputStream errorConsumer) throws IOException {
       Process process = Runtime.getRuntime()
                .exec(new String[] {
                        "yt-dlp",
                        "--add-headers",
                        "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0",
                        "--add-headers", "Accept: */*",
                        "--add-headers", "Accept-Language: en-US,en;q=0.9",
                        // "--add-headers", "Accept-Encoding: gzip, deflate, br, zstd",
                        "--add-headers", "Origin: https://tryembed.us.cc",
                        "--add-headers", "Referer: https://tryembed.us.cc/",
                        "--add-headers", "Connection: keep-alive",
                        "--add-headers", "Sec-Fetch-Dest: empty",
                        "--add-headers", "Sec-Fetch-Mode: cors",
                        "--add-headers", "Sec-Fetch-Site: cross-site",
                        "--add-headers", "TE: trailers",
                        "--fragment-retries", "infinite",
                        "-o", dest, url
                });

        service.submit(() -> process.getInputStream().transferTo(progressConsumer));
        service.submit(() -> process.getErrorStream().transferTo(errorConsumer));

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

}
