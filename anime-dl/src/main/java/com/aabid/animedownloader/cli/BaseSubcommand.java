package com.aabid.animedownloader.cli;

import static picocli.CommandLine.Help.Ansi.AUTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aabid.animedownloader.cli.mixin.LoggingMixIn;
import com.aabid.animedownloader.cli.mixin.TimeoutMixIn;
import com.aabid.animedownloader.service.animedl.ProgramConfiguration;
import com.aabid.animedownloader.service.animedl.ProgramConfiguration.Builder;
import com.aabid.animedownloader.service.animedl.ProgramServices;
import com.aabid.animedownloader.service.animedl.ProgramServicesFactory;

import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

abstract class BaseSubcommand implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(BaseSubcommand.class);

    @Spec
    private CommandSpec spec;

    @Mixin
    private LoggingMixIn loggingMixIn;

    @Mixin
    private TimeoutMixIn timeoutMixIn;

    @NonNull
    private ProgramServicesFactory factory;

    private PrintWriter out;
    private PrintWriter err;

    public BaseSubcommand(@NonNull ProgramServicesFactory factory) {
        this.factory = factory;
    }

    @Override
    public Integer call() throws Exception {
        loggingMixIn.configureLogging();

        ProgramConfiguration.Builder builder = ProgramConfiguration.builder();
        builder.err(spec.commandLine().getErr());
        builder.out(spec.commandLine().getOut());

        timeoutMixIn.applyConfiguration(builder);
        this.applyConfiguration(builder);

        ProgramServices services = factory.apply(builder.build());
        this.err = services.getErr();
        this.out = services.getOut();

        try {
            return start(services);
        } catch (UnknownHostException e) {
            printError("Unable to reach the server. Please check your internet connection and try again.");
            printStackTrace(e);
        } catch (ConnectException e) {
            printError(
                "Unable to connect to the server. Please check your internet connection, " +
                "verify the service is online, and ensure no firewall or proxy is blocking access. " +
                "If the problem continues, try again later."
            );
            printStackTrace(e);
        } catch (SocketTimeoutException e) {
            printError(
                """
                The operation timed out.
                - If connecting to the server took too long, try increasing --connect-timeout
                - If waiting for a response took too long, try increasing --read-timeout
                - If sending the request took too long, try increasing --write-timeout
                Please adjust as needed and try again.
                """
            );
            printStackTrace(e);
        } catch (IOException e) {
            printError(
                """
                An error occurred while reading or writing data.
                > This could be due to a problem with your disk, file permissions, interrupted operation, or temporary system issue.
                > Please check:
                - That you have sufficient disk space and write permissions
                - That the target file or directory is not locked or in use by another program
                - That your storage device is functioning properly

                If the problem continues, try again later or consult the system logs.
                """
            );
            printStackTrace(e);
        } catch (Exception e) {
            printError(String.format(
                """
                An unexpected error occurred: %s
                Please report this issue at:
                https://github.com/Abiddarris/anime-downloader/issues
                Use --verbose to print the full stack trace when reporting.
                """, e.toString()
            ));
            printStackTrace(e);
        }

        return 1;
    }

    protected void applyConfiguration(Builder builder) {
    }

    protected PrintWriter getOut() {
        return out;
    }

    protected PrintWriter getErr() {
        return err;
    }

    protected abstract int start(ProgramServices services) throws Exception;

    protected void printStackTrace(Throwable throwable) {
        log.debug("Detailed Stacktrace: ", throwable);
    }

    protected void printError(String message) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            err.println(AUTO.string("@|red,bold [ERROR]: " + line + "|@"));
        }
    }
}
