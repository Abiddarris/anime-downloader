package com.aabid.animedownloader.m3u8;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.aabid.animedownloader.service.ytdlp.DownloadConfiguration;
import com.aabid.animedownloader.service.ytdlp.Retries;
import com.aabid.animedownloader.service.ytdlp.YtDlpInvocationException;
import com.aabid.animedownloader.service.ytdlp.YtDlpService;
import com.aabid.animedownloader.utils.program.ProgramInvoker;

public class YtDlpM3U8Downloader implements M3U8Downloader {

    private final ProgramInvoker invoker;

    public YtDlpM3U8Downloader(ProgramInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void download(String url, String dest,
                        OutputStream progressConsumer, OutputStream errorConsumer) throws IOException {
        List<String> headers = new ArrayList<>();
        headers.add("User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:151.0) Gecko/20100101 Firefox/151.0");
        headers.add("Accept: */*");
        headers.add("Accept-Language: en-US,en;q=0.9");
        // headers.add("Accept-Encoding: gzip, deflate, br, zstd");
        headers.add("Origin: https://tryembed.us.cc");
        headers.add("Referer: https://tryembed.us.cc/");
        headers.add("Connection: keep-alive");
        headers.add("Sec-Fetch-Dest: empty");
        headers.add("Sec-Fetch-Mode: cors");
        headers.add("Sec-Fetch-Site: cross-site");
        headers.add("TE: trailers");

        DownloadConfiguration configuration = new DownloadConfiguration.Builder()
            .setHeaders(headers)
            .setFragmentRetries(Retries.infinite())
            .setBuffersize(1024 * 16)
            .build();

        YtDlpService service = new YtDlpService(invoker);
        try {
            service.download(configuration, url, new File(dest).toPath());
        } catch (YtDlpInvocationException | InterruptedException e) {
            throw new IOException(e);
        }
    }

}
