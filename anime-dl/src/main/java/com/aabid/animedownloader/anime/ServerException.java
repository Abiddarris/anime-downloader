package com.aabid.animedownloader.anime;

public class ServerException extends AnimeServiceException {

    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
