/*
 * Copyright 2026 Aabid Darris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
