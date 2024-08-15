package net.jmp.demo.java22;

/*
 * (#)StructuredConcurrencyDemo.java    0.6.0   08/15/2024
 *
 * @author   Jonathan Parker
 * @version  0.6.0
 * @since    0.6.0
 *
 * MIT License
 *
 * Copyright (c) 2024 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.time.Instant;

import java.util.List;
import java.util.UUID;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;

import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A class the demonstrates using structured concurrency.
 */
final class StructuredConcurrencyDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    StructuredConcurrencyDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.shutdownOnFailure();
        this.shutdownOnSuccess();

        this.logger.exit();
    }

    /**
     * Get the value or handle an exception.
     */
    private void shutdownOnFailure() {
        this.logger.entry();

        try {
            final Response response = this.getResponse();

            this.logger.info("User: {}; Order: {}", response.user, response.orderNumber);
        } catch (final ExecutionException | InterruptedException e) {
            this.logger.catching(e);

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        this.logger.exit();
    }

    /**
     * Get the value of the first callable that succeeds
     */
    private void shutdownOnSuccess() {
        this.logger.entry();

        final List<Callable<String>> tasks = List.of(
                () -> UUID.randomUUID().toString(),
                () -> "Jonathan",
                () -> "Martin",
                () -> "Parker"
        );

        try {
            final var result = this.runAll(tasks, Instant.now().plusMillis(10));

            this.logger.info("Result: {}", result);
        } catch (final ExecutionException | InterruptedException | TimeoutException e) {
            this.logger.catching(e);

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        this.logger.exit();
    }

    /**
     * Returns the value of the
     * first callable that succeeds.
     *
     * @param   tasks
     * @param   instant
     * @return
     * @throws  ExecutionException
     * @throws  InterruptedException
     * @throws  TimeoutException
     */
    private String runAll(final List<Callable<String>> tasks, final Instant instant) throws ExecutionException, InterruptedException, TimeoutException {
        this.logger.entry(tasks, instant);

        String result = null;

        try (final var scope = new StructuredTaskScope.ShutdownOnSuccess<>()) {
            for (final var task : tasks) {
                scope.fork(task);
            }

            result = (String) scope.joinUntil(instant).result();
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Create and return a response object.
     *
     * @return  net.jmp.demo.java22.StructuredConcurrencyDemo.Response
     * @throws  java.util.concurrent.ExecutionException
     * @throws  java.lang.InterruptedException
     */
    private Response getResponse() throws ExecutionException, InterruptedException {
        this.logger.entry();

        Response response = null;

        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final Supplier<String> user = scope.fork(() -> findUser());
            final Supplier<Integer> orderNumber = scope.fork(() -> findOrderNumber());

            scope.join()                // Wait for both subtasks
                    .throwIfFailed();   // Propagate exceptions

            response = new Response(user.get(), orderNumber.get());
        }

        this.logger.exit(response);

        return response;
    }

    /**
     * Return the user.
     *
     * @return  java.lang.String
     */
    private String findUser() {
        return "Jonathan";
    }

    /**
     * Return the order number.
     *
     * @return  java.lang.Integer
     */
    private Integer findOrderNumber() {
        return 123;
    }

    /**
     * A response record.
     *
     * @param   user        java.lang.String
     * @param   orderNumber java.lang.Integer
     */
    record Response(String user, Integer orderNumber) {}
}
