package com.aabid.animedownloader.net;

public class TimeoutException extends NetworkException {

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
