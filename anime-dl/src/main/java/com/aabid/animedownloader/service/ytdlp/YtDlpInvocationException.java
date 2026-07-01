package com.aabid.animedownloader.service.ytdlp;

public class YtDlpInvocationException extends Exception {

    private int exitCode;
    private String errorOutput;

    public YtDlpInvocationException(int exitCode, String errorOutput) {
        super("Yt-Dlp exit with non zero code (" + exitCode + ") with error output : " + errorOutput);

        this.exitCode = exitCode;
        this.errorOutput = errorOutput;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

}
