package net.jmp.demo.java22.util;

/*
 * (#)KeyedFunctionExecutor.java    0.2.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.2.0
 * @since    0.2.0
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.*;

import java.util.function.Function;

/**
 * The keyed function executor.
 *
 * @param   <T> The type of value
 */
public final class KeyedFunctionExecutor<T> {
    private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    /** The map of keyed entries. */
    private final Map<String, T> map = new ConcurrentHashMap<>();

    /** The executor service. */
    private final ExecutorService executor;

    /** A list of runnable futures. */
    private final List<Future<Void>> futures = new ArrayList<>();

    /** True once the start method has been invoked. */
    private boolean isStarted;

    /**
     * The default constructor.
     */
    public KeyedFunctionExecutor() {
        super();

        this.executor = Executors.newFixedThreadPool(DEFAULT_NUMBER_OF_THREADS);
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    public KeyedFunctionExecutor(final int numberOfThreads) {
        super();

        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than 0");
        }

        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Start the executor.
     */
    public void start() {
        this.isStarted = true;
    }

    /**
     * Stop the executor.
     */
    public void stop() {
        this.futures.forEach(future -> {
            if (!future.isDone()) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }

                    e.printStackTrace(System.err);
                }
            }
        });

        this.futures.clear();

        this.executor.shutdown();

        this.isStarted = false;
    }

    /**
     * Process the keyed function.
     *
     * @param   function    java.util.function.Function&lt;T, java.lang.Void&gt;
     * @param   key         java.lang.String
     * @param   value       T
     */
    public void process(final Function<T, Void> function, final String key, final T value) {
        if (!this.isStarted) {
            throw new IllegalStateException("This KeyedFunctionExecutor was not started");
        }

        this.map.put(key, value);

        final T val = map.get(key);

        this.map.remove(key);

        final Future<Void> future = this.executor.submit(() -> function.apply(val));

        this.futures.add(future);
    }
}
