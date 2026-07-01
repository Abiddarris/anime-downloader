package com.aabid.animedownloader.service.ytdlp;

import com.aabid.animedownloader.net.DNSException;
import com.aabid.animedownloader.net.NetworkException;
import com.aabid.animedownloader.net.TimeoutException;

class ExceptionTranslator {

    static void translate(YtDlpInvocationException e) throws YtDlpInvocationException, NetworkException {
        String message = e.getErrorOutput();
        if (message.contains("Failed to resolve")) {
            throw new DNSException("Unable to resolve host", e);
        }

        if (message.contains("Read timed out")) {
            throw new TimeoutException("Timeout", e);
        }

        throw e;
    }
}
