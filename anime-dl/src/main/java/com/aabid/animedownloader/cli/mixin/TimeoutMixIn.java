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
package com.aabid.animedownloader.cli.mixin;

import com.aabid.animedownloader.service.animedl.ProgramConfiguration;

import picocli.CommandLine.Option;

/**
 * Mixin class for configuring network timeout values via command-line options.
 * Provides options to set connect, read, and write timeouts for HTTP requests.
 */
public class TimeoutMixIn {

    /**
     * Connection timeout in milliseconds.
     * <p>
     * Specifies the time to wait for a connection to be established.
     * Use 0 for no timeout (infinite wait) - not recommended for most use cases.
     * </p>
     */
    private int connectTimeout;

    /**
     * Read timeout in milliseconds.
     * <p>
     * Specifies the time to wait for data to be available for reading.
     * Use 0 for no timeout (infinite wait) - not recommended for most use cases.
     * </p>
     */
    private int readTimeout;

    /**
     * Write timeout in milliseconds.
     * <p>
     * Specifies the time to wait for data to be written to the socket.
     * Use 0 for no timeout (infinite wait) - not recommended for most use cases.
     * </p>
     */
    private int writeTimeout;

    /**
     * Applies the timeout values from this mixin to the given configuration builder.
     *
     * @param builder the configuration builder to apply timeout values to
     */
    public void applyConfiguration(ProgramConfiguration.Builder builder) {
        builder.connectTimeout(connectTimeout);
        builder.readTimeout(readTimeout);
        builder.writeTimeout(writeTimeout);
    }

    /**
     * Gets the connection timeout value.
     *
     * @return the connection timeout in milliseconds (0 means not set)
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Gets the read timeout value.
     *
     * @return the read timeout in milliseconds (0 means not set)
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Gets the write timeout value.
     *
     * @return the write timeout in milliseconds (0 means not set)
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * Sets the connection timeout value.
     *
     * @param connectTimeout the connection timeout in milliseconds (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    @Option(
        names = { "--connect-timeout" },
        defaultValue = "10000",
        paramLabel = "mills",
        description = "Connection timeout in milliseconds. 0 for no timeout (default: ${DEFAULT-VALUE})"
    )
    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("connectTimeout cannot be negative");
        }
        this.connectTimeout = connectTimeout;
    }

    /**
     * Sets the read timeout value.
     *
     * @param readTimeout the read timeout in milliseconds (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    @Option(
        names = { "--read-timeout" },
        defaultValue = "30000",
        paramLabel = "mills",
        description = "Read timeout in milliseconds. 0 for no timeout (default: ${DEFAULT-VALUE})"
    )
    public void setReadTimeout(int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("readTimeout cannot be negative");
        }
        this.readTimeout = readTimeout;
    }

    /**
     * Sets the write timeout value.
     *
     * @param writeTimeout the write timeout in milliseconds (must be >= 0)
     * @throws IllegalArgumentException if the value is negative
     */
    @Option(
        names = {"--write-timeout" },
        defaultValue = "10000",
        paramLabel = "mills",
        description = "Write timeout in milliseconds. 0 for no timeout (default: ${DEFAULT-VALUE})"
    )
    public void setWriteTimeout(int writeTimeout) {
        if (writeTimeout < 0) {
            throw new IllegalArgumentException("writeTimeout cannot be negative");
        }
        this.writeTimeout = writeTimeout;
    }
}