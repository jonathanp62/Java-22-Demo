package net.jmp.demo.java22.util;

/*
 * (#)AppliedBaseCollection.java    0.7.0   08/20/2024
 * (#)AppliedBaseCollection.java    0.6.0   08/16/2024
 * (#)AppliedBaseCollection.java    0.5.0   08/10/2024
 * (#)AppliedBaseCollection.java    0.4.0   08/09/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.0
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
import java.util.Collection;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A base class for applied collections.
 *
 * @param   <T> The type of element
 */
public class AppliedBaseCollection<T> {
    private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** The executor service. */
    protected final ExecutorService executor;

    /** A list of runnable futures. */
    protected final List<Future<?>> futures = new ArrayList<>();

    /**
     * The default constructor.
     */
    protected AppliedBaseCollection() {
        super();

        this.executor = Executors.newFixedThreadPool(DEFAULT_NUMBER_OF_THREADS);
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    protected AppliedBaseCollection(final int numberOfThreads) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than 0");
        }

        super();

        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Close any resources. In this case wait
     * for futures to complete and shut down
     * the executor service.
     */
    protected void close() {
        this.logger.entry();

        this.waitForFutures();
        this.executor.shutdown();

        this.logger.exit();
    }

    /**
     * Adds all the elements in the specified collection to this list.
     * Apply the mapper function to each element before adding it.
     *
     * @param   target  java.util.Collection&lt;T&gt;
     * @param   source  java.util.Collection&lt;? extends T&gt;
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    protected boolean applyAndAddAll(@Nonnull final Collection<T> target,
                                     @Nonnull final Collection<? extends T> source,
                                     final Function<? super T, ? extends T> mapper) {
        this.logger.entry(target, source, mapper);

        final WrappedObject<Boolean> result = WrappedObject.of(false);

        if (!source.isEmpty()) {
            source.forEach(e -> {
                target.add(mapper.apply(e));
                result.set(true);
            });
        }

        this.logger.exit(result.get());

        return result.get();
    }

    /**
     * Apply the onElement to each element
     * and then clear the collection.
     *
     * @param   collection  java.util.Collection&lt;T&gt;
     * @param   onElement   java.util.function.Consumer&lt;T&gt;
     * @param   onEnd       java.lang.Runnable
     */
    protected void clearAndApply(final Collection<T> collection, final Consumer<T> onElement, final Runnable onEnd) {
        this.logger.entry(collection, onElement, onEnd);

        collection.forEach(e -> {
            if (e != null) {
                this.runTask(() -> onElement.accept(e));
            }
        });

        collection.clear();

        onEnd.run();

        this.logger.exit();
    }

    /**
     * Removes all of this collection's elements that are also contained in the specified
     * collection (optional operation). After this call returns, this collection will contain
     * no elements in common with the specified collection.
     * Apply the onElement consumer to each removed element.
     *
     * @param   target      java.util.Collection&lt;T&gt;
     * @param   source      java.util.Collection&lt;? extends T&gt;
     * @param   onElement   java.util.function.Consumer&lt;T&gt;
     * @param   onEnd       java.lang.Runnable
     * @return              boolean
     */
    protected boolean removeAllAndApply(@Nonnull final Collection<T> target,
                                        @Nonnull final Collection<? extends T> source,
                                        final Consumer<T> onElement,
                                        final Runnable onEnd) {
        this.logger.entry(target, source, onElement, onEnd);

        final WrappedObject<Boolean> result = WrappedObject.of(false);

        if (!source.isEmpty()) {
            source.forEach(e -> {
                if (target.contains(e) && target.remove(e)) {
                    this.runTask(() -> onElement.accept(e));
                    result.set(true);
                }
            });
        }

        onEnd.run();

        this.logger.exit(result.get());

        return result.get();
    }

    /**
     * Run the task by submitting the
     * runnable to the executor service.
     *
     * @param   task    java.lang.Runnable
     */
    protected void runTask(final Runnable task) {
        this.logger.entry(task);

        this.futures.add(this.executor.submit(task));

        this.logger.exit();
    }

    /**
     * Wait for any futures to complete.
     */
    private void waitForFutures() {
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

        this.logger.exit();
    }
}
