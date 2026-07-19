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
    public Program invoke(Path workingDirectory, StreamConsumer output, StreamConsumer error, String... args)
            throws IOException {
        List<String> arguments = new LinkedList<>();
        arguments.add(program);
        arguments.addAll(List.of(args));

        ProcessBuilder builder = new ProcessBuilder(arguments);
        builder.directory(workingDirectory.toFile());

        Process process = builder.start();
        Future<Void> outputFuture = executor.submit(() -> {
            output.consume(process.getInputStream());
            return null;
        });

        Future<Void> errorFuture = executor.submit(() -> {
            error.consume(process.getErrorStream());
            return null;
        });

        return new ProgramImpl(process, outputFuture, errorFuture);
    }

    private static class ProgramImpl implements Program {

        private Process process;
        private Future<Void> outputFuture;
        private Future<Void> errorFuture;

        public ProgramImpl(
            Process process, Future<Void> outputFuture, Future<Void> errorFuture
        ) {
            this.process = process;
            this.outputFuture = outputFuture;
            this.errorFuture = errorFuture;
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
