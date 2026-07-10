package com.aabid.animedownloader.cli;

import com.aabid.animedownloader.service.animedl.ProgramConfiguration;

import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Visibility;

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
        showDefaultValue = Visibility.ALWAYS,
        description = "Connection timeout in milliseconds (0 for no timeout)"
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
        showDefaultValue = Visibility.ALWAYS,
        description = "Read timeout in milliseconds (0 for no timeout)"
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
        showDefaultValue = Visibility.ALWAYS,
        description = "Write timeout in milliseconds (0 for no timeout)"
    )
    public void setWriteTimeout(int writeTimeout) {
        if (writeTimeout < 0) {
            throw new IllegalArgumentException("writeTimeout cannot be negative");
        }
        this.writeTimeout = writeTimeout;
    }
}