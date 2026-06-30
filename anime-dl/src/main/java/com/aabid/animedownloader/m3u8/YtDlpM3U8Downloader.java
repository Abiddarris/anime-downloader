package com.aabid.animedownloader.m3u8;

import java.io.IOException;
import java.io.OutputStream;

import com.aabid.animedownloader.utils.program.Program;
import com.aabid.animedownloader.utils.program.ProgramInvoker;
import com.aabid.animedownloader.utils.program.StreamConsumer;

public class YtDlpM3U8Downloader implements M3U8Downloader {

    private final ProgramInvoker invoker;

    public YtDlpM3U8Downloader(ProgramInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void download(String url, String dest,
                        OutputStream progressConsumer, OutputStream errorConsumer) throws IOException {
        StreamConsumer output = (out) -> out.transferTo(progressConsumer);
        StreamConsumer error = (err) -> err.transferTo(errorConsumer);

        Program program = invoker.invoke(output, error,
    "--add-headers", "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0",
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
        );

        try {
            int exitCode = program.getExitCode();
            if (exitCode != 0) {
                throw new IOException("yt-dlp exit with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

}
