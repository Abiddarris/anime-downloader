package com.aabid.animedownloader.utils.program;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultProgramInvoker implements ProgramInvoker {

    private String program;
    private ExecutorService executor;

    public DefaultProgramInvoker(String program, ExecutorService executor) {
        Objects.requireNonNull(program, "program must not be null");
        Objects.requireNonNull(executor, "executor must not be null");

        this.program = program;
        this.executor = executor;
    }

    @Override
    public <T extends StreamConsumer> Program<T> invoke(
        Path workingDirectory, String[] args, StreamConsumerFactory<T> factory
    ) throws IOException {
        List<String> arguments = new LinkedList<>();
        arguments.add(program);
        arguments.addAll(List.of(args));

        ProcessBuilder builder = new ProcessBuilder(arguments);
        builder.directory(workingDirectory.toFile());

        Process process = builder.start();

        T output = factory.newStreamConsumer();
        T error = factory.newStreamConsumer();

        Future<Void> outputFuture = executor.submit(() -> {
            output.consume(process.getInputStream());
            return null;
        });

        Future<Void> errorFuture = executor.submit(() -> {
            error.consume(process.getErrorStream());
            return null;
        });

        return new ProgramImpl<T>(
            process, output, error, outputFuture, errorFuture
        );
    }

    private static class ProgramImpl<T extends StreamConsumer> implements Program<T> {

        private Process process;
        private T output;
        private T error;
        private Future<Void> outputFuture;
        private Future<Void> errorFuture;

        public ProgramImpl(
            Process process, T output, T error,
            Future<Void> outputFuture, Future<Void> errorFuture
        ) {
            this.process = process;
            this.output = output;
            this.error = error;
            this.outputFuture = outputFuture;
            this.errorFuture = errorFuture;
        }

        @Override
        public T getOutputStreamConsumer() {
            return output;
        }

        @Override
        public T getErrorStreamConsumer() {
            return error;
        }

        @Override
        public int getExitCode() throws InterruptedException {
            if (process.isAlive()) {
                checkStreamConsumer();
            }

            int exitCode = process.waitFor();
            try {
                outputFuture.get();
                errorFuture.get();
            } catch (ExecutionException e) {
                throw new StreamConsumerException(e.getCause());
            }

            return exitCode;
        }

        private void checkStreamConsumer() throws InterruptedException {
            Throwable t = getException(outputFuture);
            if (t != null) {
                throw new StreamConsumerException(t);
            }

            t = getException(errorFuture);
            if (t != null) {
                throw new StreamConsumerException(t);
            }
        }

        private Throwable getException(Future<Void> future) throws InterruptedException {
            if (!future.isDone()) {
                return null;
            }

            try {
                future.get();
                return null;
            } catch (ExecutionException e) {
                return e.getCause();
            }
        }

    }
}
