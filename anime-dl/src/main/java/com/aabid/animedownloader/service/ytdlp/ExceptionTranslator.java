package com.aabid.animedownloader.service.ytdlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aabid.animedownloader.net.DNSException;
import com.aabid.animedownloader.net.NetworkException;
import com.aabid.animedownloader.net.TimeoutException;

class ExceptionTranslator {

    private static final Pattern HTTP_ERROR_PATTERN = Pattern.compile("HTTP Error ([0-9]{3}): (.*)");

    static void translate(YtDlpInvocationException e) throws YtDlpInvocationException, NetworkException, HttpException {
        String message = e.getErrorOutput();
        if (message.contains("Failed to resolve")) {
            throw new DNSException("Unable to resolve host", e);
        }

        if (message.contains("Read timed out")) {
            throw new TimeoutException("Timeout", e);
        }

        checkHttpErrorMessage(message, e);

        throw e;
    }

    private static void checkHttpErrorMessage(String message, YtDlpInvocationException e) throws HttpException {
        Matcher matcher = HTTP_ERROR_PATTERN.matcher(message);
        if (!matcher.find()) {
            return;
        }

        int errorCode = Integer.valueOf(matcher.group(1));
        String errorMessage = matcher.group(2);

        throw new HttpException(errorCode, errorMessage, e);
    }
}
