package com.aabid.animedownloader.service.ytdlp;

public class HttpException extends Exception {

    private int errorCode;
    private String errorMessage;

    public HttpException(int errorCode, String errorMessage, Throwable cause) {
        super(String.format("Http code %s: %s", errorCode, errorMessage), cause);

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
