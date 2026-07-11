package com.aabid.animedownloader.anime;

public class AnimeServiceException extends Exception {

    public AnimeServiceException() {
    }

    public AnimeServiceException(String message) {
        super(message);
    }

    public AnimeServiceException(Throwable cause) {
        super(cause);
    }

    public AnimeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
