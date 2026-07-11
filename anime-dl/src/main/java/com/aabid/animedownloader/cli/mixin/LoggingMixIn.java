package com.aabid.animedownloader.cli.mixin;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import picocli.CommandLine.Option;

public class LoggingMixIn {

    @Option(names = { "-v", "--verbose" }, description = "Enable debug logging")
    private boolean verbose;

    public void configureLogging() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (verbose) {
            root.setLevel(Level.DEBUG);
        }
    }
}
