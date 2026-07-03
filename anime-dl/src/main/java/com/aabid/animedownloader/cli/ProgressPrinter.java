package com.aabid.animedownloader.cli;

import java.io.PrintWriter;

import com.aabid.animedownloader.service.ytdlp.Progress;
import com.aabid.animedownloader.service.ytdlp.Progress.State;
import com.aabid.animedownloader.utils.ByteFormat;

class ProgressPrinter {

    private PrintWriter out;

    public ProgressPrinter(PrintWriter out) {
        this.out = out;
    }

    public void onProgressUpdate(Progress progress) {
        if (progress.getState() == State.FINISHED) {
            out.print("\r[Status] Download completed successfully.\033[K\n");
            out.flush();

            return;
        }

        long totalBytes = getTotalBytes(progress);
        String sizeType = progress.getTotalEstimate() == null ? "" : "~";

        double percent = totalBytes > 0 ? ((double) progress.getDownloaded() / totalBytes) * 100.0 : 0;

        String downloadedStr = ByteFormat.formatBytes(progress.getDownloaded());
        String totalStr = totalBytes > 0 ? sizeType + ByteFormat.formatBytes(totalBytes) : "Unknown Size";
        String speedStr = getSpeedStr(progress);
        String fragmentStr = getFragmentStr(progress);

        out.print(String.format("\r[Downloading] %.1f%% (%s / %s) at %s%s\033[K",
                percent, downloadedStr, totalStr, speedStr, fragmentStr));
        out.flush();
    }

    private long getTotalBytes(Progress progress) {
        Long total = progress.getTotal();
        Long totalEstimate = progress.getTotalEstimate();

        if (total != null) {
            return total;
        } else if (totalEstimate != null) {
            return totalEstimate;
        }

        return 0;
    }

    private String getSpeedStr(Progress progress) {
        Long speed = progress.getSpeed();
        if (speed != null && speed > 0) {
            return ByteFormat.formatBytes(speed) + "/s";
        }

        return "N/A";
    }

    private String getFragmentStr(Progress progress) {
        Long fragmentIndex = progress.getFragmentIndex();
        Long fragmentCount = progress.getFragmentCount();
        if (fragmentIndex != null && fragmentCount != null) {
            return String.format(" (Frag %d/%d)", fragmentIndex, fragmentCount);
        }

        return "";
    }
}
