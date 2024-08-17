package net.jmp.demo.java22.demos;

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

import java.util.concurrent.*;

import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A class the demonstrates using structured concurrency.
 *
 * <a href="https://openjdk.org/jeps/462">JEP 462: Structured Concurrency (Second Preview)</a>
 */
public final class StructuredConcurrencyDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    public StructuredConcurrencyDemo() {
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
        this.noShutdownPolicy();

        this.logger.exit();
    }

    /**
     * No shutdown policy. Collect a list
     * of each task's respective success
     * or failure.
     */
    private void noShutdownPolicy() {
        this.logger.entry();

        final List<Callable<Integer>> tasks = List.of(
                () -> 1 + 0,
                () -> 2 - 0,
                () -> 1 / 0,
                () -> 3 * 0
        );

        try {
            final List<Future<Integer>> futures = this.executeAll(tasks);

            futures.forEach(future -> {
                try {
                    this.logger.info("Future: {}", future.get());
                } catch (final ExecutionException | InterruptedException e) {
                    this.logger.error("Future: {}", e.getMessage());

                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        } catch (final InterruptedException e) {
            this.logger.catching(e);
            Thread.currentThread().interrupt();
        }

        this.logger.exit();
    }

    /**
     * If the scope owner processes subtask exceptions to produce a composite
     * result, rather than use a shutdown policy, then exceptions can be
     * returned as values from the subtasks. For example, here is a method
     * that runs a list of tasks in parallel and returns a list of completed
     * Futures containing each task's respective successful or exceptional result.
     *
     * @param   tasks   java.util.List&lt;java.util.concurrent.Callable&lt;java.lang.Integer&gt;&gt;
     * @return          java.util.List&lt;java.util.concurrent.Future&lt;java.lang.Integer&gt;&gt;
     * @throws          java.lang.InterruptedException
     */
    private List<Future<Integer>> executeAll(final List<Callable<Integer>> tasks) throws InterruptedException {
        this.logger.entry(tasks);

        List<Future<Integer>> results;

        try (final var scope = new StructuredTaskScope<Future<Integer>>()) {
            final List<? extends Supplier<Future<Integer>>> futures = tasks.stream()
                    .map(this::taskAsFuture)
                    .map(scope::fork)
                    .toList();

            scope.join();

            results = futures.stream()
                    .map(Supplier::get)
                    .toList();
        }

        this.logger.exit(results);

        return results;
    }

    /**
     * Convert a callable task into a completable future.
     *
     * @param   <T>     The type of callable
     * @param   task    java.util.concurrent.Callable
     * @return          java.util.concurrent.Callable&lt;java.util.concurrent.Future&lt;T&gt;&gt;
     */
    private <T> Callable<Future<T>> taskAsFuture(final Callable<T> task) {
        return () -> {
            try {
                return CompletableFuture.completedFuture(task.call());
            } catch (final Exception ex) {
                return CompletableFuture.failedFuture(ex);
            }
        };
    }

    /**
     * Get the value or handle an exception.
     */
    private void shutdownOnFailure() {
        this.logger.entry();

        // First case

        try {
            final Response response = this.getResponse();

            this.logger.info("User: {}; Order: {}", response.user, response.orderNumber);
        } catch (final ExecutionException | InterruptedException e) {
            this.logger.catching(e);

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        // Second case

        final List<Callable<String>> tasks = List.of(
                () -> "Red",
                () -> "Orange",
                () -> "Yellow",
                () -> "Green",
                () -> "Blue",
                () -> "Indigo",
                () -> "Violet"
        );

        try {
            final var results = this.runAll(tasks);

            results.forEach(this.logger::info);
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);
            Thread.currentThread().interrupt();
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
            final var result = this.race(tasks, Instant.now().plusMillis(10));

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
     * @param   tasks   java.util.List&lt;java.util.Callable&lt;java.lang.String&gt;&gt;
     * @param   instant java.time.Instant
     * @return          java.lang.String
     * @throws          java.util.concurrent.ExecutionException
     * @throws          java.lang.InterruptedException
     * @throws          java.util.concurrent.TimeoutException
     */
    private String race(final List<Callable<String>> tasks, final Instant instant) throws ExecutionException, InterruptedException, TimeoutException {
        this.logger.entry(tasks, instant);

        String result;

        // Avoid processing subtask results returned by fork()
        // using the shutdown on success policy

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
     * Return the results of all the tasks
     * only failing if one of them should fail.
     *
     * @param   tasks   java.util.List&lt;java.util.Callable&lt;java.lang.String&gt;&gt;
     * @return          java.util.List&lt;java.lang.String&gt;
     * @throws          java.lang.InterruptedException
     */
    private List<String> runAll(final List<Callable<String>> tasks) throws InterruptedException {
        this.logger.entry(tasks);

        List<String> results;

        // Note that the return type of fork is a supplier

        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final List<? extends Supplier<String>> suppliers = tasks
                    .stream()
                    .map(scope::fork)
                    .toList();

            // The IfFailed() function is invoked for the first subtask to fail
            // This is known as a supplying function, but it is not a supplier

            scope.join().throwIfFailed(exception -> {
                this.logger.catching(exception);

                return new RuntimeException(exception.getMessage());
            });

            results = suppliers
                    .stream()
                    .map(Supplier::get)
                    .toList();
        }

        this.logger.exit(results);

        return results;
    }

    /**
     * Create and return a response object or
     * fail if any of the subtasks fail.
     *
     * @return  net.jmp.demo.java22.demos.StructuredConcurrencyDemo.Response
     * @throws  java.util.concurrent.ExecutionException
     * @throws  java.lang.InterruptedException
     */
    private Response getResponse() throws ExecutionException, InterruptedException {
        this.logger.entry();

        Response response = null;

        // Note that the return type of fork is a supplier

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
