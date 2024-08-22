package net.jmp.demo.java22.util;

/*
 * (#)KeyedFunctionExecutor.java    0.8.0   08/22/2024
 * (#)KeyedFunctionExecutor.java    0.5.0   08/14/2024
 * (#)KeyedFunctionExecutor.java    0.4.0   08/09/2024
 * (#)KeyedFunctionExecutor.java    0.2.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.8.0
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

import com.google.common.util.concurrent.Striped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.concurrent.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import java.util.function.Function;

import static net.jmp.demo.java22.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The keyed function executor.
 *
 * @param   <T> The type of value
 */
public final class KeyedFunctionExecutor<T> implements AutoCloseable {
    private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The map of keyed entries. */
    private final Map<String, T> map = new ConcurrentHashMap<>();

    /** The executor service. */
    private final ExecutorService executor;

    /** A list of runnable futures. */
    private final List<Future<Void>> futures = new ArrayList<>();

    /** Control access to the map. */
    private final Striped<ReadWriteLock> locks = Striped.readWriteLock(64);

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
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than 0");
        }

        super();

        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Close any resources.
     */
    @Override
    public void close() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        this.waitForFutures();
        this.executor.shutdown();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Wait for any futures to complete.
     */
    private void waitForFutures() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        this.futures.forEach(future -> {
            if (!future.isDone()) {
                try {
                    future.get();
                } catch (final InterruptedException | ExecutionException e) {
                    this.logger.error("A thread incurred an exception or was interrupted", e);

                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        this.futures.clear();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Process the keyed function.
     *
     * @param   function    java.util.function.Function&lt;T, java.lang.Void&gt;
     * @param   key         java.lang.String
     * @param   value       T
     */
    public void process(final Function<T, Void> function, final String key, final T value) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(function, key, value));
        }

        Objects.requireNonNull(function);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        this.map.put(key, value);

        final Lock lock = this.locks.get(key).writeLock();

        if (lock.tryLock()) {
            try {
                while (map.containsKey(key)) {
                    final T val = map.get(key);

                    this.map.remove(key);

                    final Future<Void> future = this.executor.submit(() -> function.apply(val));

                    this.futures.add(future);
                }
            } finally {
                lock.unlock();
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
