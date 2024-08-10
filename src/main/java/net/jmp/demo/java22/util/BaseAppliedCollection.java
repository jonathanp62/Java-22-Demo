package net.jmp.demo.java22.util;

/*
 * (#)BaseAppliedCollection.java    0.5.0   08/10/2024
 * (#)BaseAppliedCollection.java    0.4.0   08/09/2024
 *
 * @author   Jonathan Parker
 * @version  0.5.0
 * @since    0.4.0
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A base class for applied collections.
 *
 * @param   <T> The type of element
 */
public class BaseAppliedCollection<T> {
    private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** The executor service. */
    protected final ExecutorService executor;

    /** A list of runnable futures. */
    protected final List<Future<?>> futures = new ArrayList<>();

    /** True once the start method has been invoked. */
    private boolean isStarted;

    /**
     * The default constructor.
     */
    protected BaseAppliedCollection() {
        super();

        this.executor = Executors.newFixedThreadPool(DEFAULT_NUMBER_OF_THREADS);
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    protected BaseAppliedCollection(final int numberOfThreads) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than 0");
        }

        super();

        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Start the executor.
     */
    public void start() {
        this.logger.entry();

        this.isStarted = true;

        this.logger.exit();
    }

    /**
     * Stop the executor.
     */
    public void stop() {
        this.logger.entry();

        this.futures.forEach(future -> {
            if (!future.isDone()) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    this.logger.catching(e);

                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        this.futures.clear();

        this.executor.shutdown();

        this.isStarted = false;

        this.logger.exit();
    }

    /**
     * Return true if the applied collection has been started.
     */
    protected boolean isStarted() {
        return !this.isStarted;
    };
}
