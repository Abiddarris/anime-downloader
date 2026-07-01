package com.aabid.animedownloader.net;

public class DNSException extends NetworkException {

    public DNSException(String message) {
        super(message);
    }

    public DNSException(String message, Throwable cause) {
        super(message, cause);
    }

}
