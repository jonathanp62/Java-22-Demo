package net.jmp.demo.java22.demos;

/*
 * (#)StructuredConcurrencyDemo.java    0.7.0   08/18/2024
 * (#)StructuredConcurrencyDemo.java    0.6.0   08/15/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.0
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

import java.util.stream.Stream;

import net.jmp.demo.java22.scopes.CustomScope;

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
        this.customPolicy();

        this.logger.exit();
    }

    /**
     * Use the custom scope as a policy.
     */
    private void customPolicy() {
        this.logger.entry();

        final List<Callable<Integer>> tasks = List.of(
                () -> 1 + 0,
                () -> 2 - 0,
                () -> 3 / 0,
                () -> 4 * 0,
                () -> 5,
                () -> 6 /0
        );

        try {
            final var scopeResults = this.allResultsAndThrowables(tasks);

            final var results = scopeResults.results;
            final var throwables = scopeResults.throwables;

            results.forEach(result -> this.logger.info("Custom result: {}", result));
            throwables.forEach(throwable -> this.logger.info("Custom throwable: {}", throwable.getMessage()));
        } catch (final InterruptedException ie) {
            this.logger.catching(ie);
            Thread.currentThread().interrupt();
        }

        this.logger.exit();
    }

    /**
     * Gather all completed results and exceptions using a custom scope.
     *
     * @param   <T>     The type of result
     * @param   tasks   java.util.concurrent.Callable&lt;T&gt;
     * @return          net.jmp.demo.java22.SturcturedConcurrenyDemo.CustomScopeResultsAndThrowables
     * @throws          java.lang.InterruptedException
     */
    private <T> CustomScopeResultsAndThrowables<T> allResultsAndThrowables(final List<Callable<T>> tasks) throws InterruptedException {
        this.logger.entry();

        List<T> results;
        List<Throwable> throwables;

        try (final var scope = new CustomScope<T>()) {
            tasks.forEach(scope::fork);

            scope.join();

            results = scope.results().toList();
            throwables = scope.throwables().toList();
        }

        final CustomScopeResultsAndThrowables<T> scopeResults = new CustomScopeResultsAndThrowables<>(results, throwables);

        this.logger.exit(scopeResults);

        return scopeResults;
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
            final Stream<Future<Integer>> futures = this.executeAll(tasks);

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
     * @return          java.util.stream.Stream&lt;java.util.concurrent.Future&lt;java.lang.Integer&gt;&gt;
     * @throws          java.lang.InterruptedException
     */
    private Stream<Future<Integer>> executeAll(final List<Callable<Integer>> tasks) throws InterruptedException {
        this.logger.entry(tasks);

        Stream<Future<Integer>> results;

        try (final var scope = new StructuredTaskScope<Future<Integer>>()) {
            // If this list is converted to a stream then a task scope is closed illegal state exception occurs

            final List<? extends Supplier<Future<Integer>>> futures = tasks.stream()
                    .map(this::taskAsFuture)
                    .map(scope::fork)
                    .toList();

            scope.join();

            results = futures.stream().map(Supplier::get);
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
            final Stream<String> results = this.runAll(tasks);

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

        try (final var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            for (final var task : tasks) {
                scope.fork(task);
            }

            result = scope.joinUntil(instant).result();
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Return the results of all the tasks
     * only failing if one of them should fail.
     *
     * @param   tasks   java.util.List&lt;java.util.Callable&lt;java.lang.String&gt;&gt;
     * @return          java.util.stream.Stream&lt;java.lang.String&gt;
     * @throws          java.lang.InterruptedException
     */
    private Stream<String> runAll(final List<Callable<String>> tasks) throws InterruptedException {
        this.logger.entry(tasks);

        Stream<String> results;

        // Note that the return type of fork is a supplier

        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // If this list is converted to a stream then a task scope is closed illegal state exception occurs

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

            results = suppliers.stream().map(Supplier::get);
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

    /**
     * A record in which to return all the results
     * and throwables collected by the custom scope.
     *
     * @param   <T>         The type of result
     * @param   results     java.util.List&lt;T&gt;
     * @param   throwables  java.util.List&lt;java.lang.Throwable&gt;
     */
    record CustomScopeResultsAndThrowables<T>(List<T> results, List<Throwable> throwables) {}
}
