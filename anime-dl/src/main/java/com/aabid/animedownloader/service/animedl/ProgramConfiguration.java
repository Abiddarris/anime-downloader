package com.aabid.animedownloader.service.animedl;

import org.jspecify.annotations.NonNull;
import java.io.PrintWriter;

/**
 * Configuration class for the Anime-Downloader program.
 * Contains settings for timeouts and other configurable parameters.
 */
public final class ProgramConfiguration {

    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private final PrintWriter out;
    private final PrintWriter err;

    private ProgramConfiguration(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.out = builder.out;
        this.err = builder.err;
    }

    /**
     * Gets the connection timeout in milliseconds.
     *
     * @return the connection timeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Gets the read timeout in milliseconds.
     *
     * @return the read timeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Gets the write timeout in milliseconds.
     *
     * @return the write timeout
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * Gets the output writer.
     *
     * @return the output writer
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * Gets the error writer.
     *
     * @return the error writer
     */
    public PrintWriter getErr() {
        return err;
    }

    /**
     * Builder class for ProgramConfiguration.
     * Provides a fluent interface for creating configuration instances.
     */
    public static final class Builder {

        private static final int DEFAULT_CONNECT_TIMEOUT = 10_000;
        private static final int DEFAULT_READ_TIMEOUT = 30_000;
        private static final int DEFAULT_WRITE_TIMEOUT = 10_000;

        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private int readTimeout = DEFAULT_READ_TIMEOUT;
        private int writeTimeout = DEFAULT_WRITE_TIMEOUT;
        private PrintWriter out = new PrintWriter(System.out, true);
        private PrintWriter err = new PrintWriter(System.err, true);

        public Builder() {
        }

        /**
         * Sets the connection timeout.
         *
         * @param connectTimeout the connection timeout in milliseconds
         * @return this builder
         */
        @NonNull
        public Builder connectTimeout(int connectTimeout) {
            if (connectTimeout < 0) {
                throw new IllegalArgumentException("connectTimeout cannot be negative");
            }
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets the read timeout.
         *
         * @param readTimeout the read timeout in milliseconds
         * @return this builder
         */
        @NonNull
        public Builder readTimeout(int readTimeout) {
            if (readTimeout < 0) {
                throw new IllegalArgumentException("readTimeout cannot be negative");
            }
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets the write timeout.
         *
         * @param writeTimeout the write timeout in milliseconds
         * @return this builder
         */
        @NonNull
        public Builder writeTimeout(int writeTimeout) {
            if (writeTimeout < 0) {
                throw new IllegalArgumentException("writeTimeout cannot be negative");
            }
            this.writeTimeout = writeTimeout;
            return this;
        }

        /**
         * Sets the output writer.
         *
         * @param out the output writer
         * @return this builder
         */
        @NonNull
        public Builder out(PrintWriter out) {
            this.out = out;
            return this;
        }

        /**
         * Sets the error writer.
         *
         * @param err the error writer
         * @return this builder
         */
        @NonNull
        public Builder err(PrintWriter err) {
            this.err = err;
            return this;
        }

        /**
         * Builds the ProgramConfiguration instance.
         *
         * @return the built ProgramConfiguration
         */
        @NonNull
        public ProgramConfiguration build() {
            return new ProgramConfiguration(this);
        }
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new Builder
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }
}