package net.jmp.demo.java22.util;

/*
 * (#)AppliedBaseCollection.java    0.9.0   08/23/2024
 * (#)AppliedBaseCollection.java    0.8.0   08/22/2024
 * (#)AppliedBaseCollection.java    0.7.1   08/22/2024
 * (#)AppliedBaseCollection.java    0.7.0   08/20/2024
 * (#)AppliedBaseCollection.java    0.6.0   08/16/2024
 * (#)AppliedBaseCollection.java    0.5.0   08/10/2024
 * (#)AppliedBaseCollection.java    0.4.0   08/09/2024
 *
 * @author   Jonathan Parker
 * @version  0.9.0
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
import java.util.function.Predicate;

import static net.jmp.demo.java22.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for applied collections.
 *
 * @param   <T> The type of element
 */
public class AppliedBaseCollection<T> {
    private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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
     * Inserts the element into the collection if the
     * applied predicate function evaluates to true.
     *
     * @param   t           T
     * @param   collection  java.util.Collection&lt;? super T&gt;
     * @param   filter      java.util.function.Predicate&lt;? super T&gt;
     * @return              boolean
     */
    protected boolean addIf(final T t,
                            final Collection<? super T> collection,
                            final Predicate<? super T> filter) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(t, collection, filter));
        }

        boolean result;

        if (filter.test(t)) {
            result = collection.add(t);
        } else {
            result = true;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Inserts the element into the collection after applying
     * the mapper function if the applied predicate
     * function evaluates to true.
     *
     * @param   t           T
     * @param   collection  java.util.Collection&lt;? super T&gt;
     * @param   mapper      java.util.function.Function&lt;? super T,? extends T&gt;
     * @param   filter      java.util.function.Predicate&lt;? super T&gt;
     * @return              boolean
     */
    protected boolean applyAndAddIf(final T t,
                                    final Collection<? super T> collection,
                                    final Function<? super T, ? extends T> mapper,
                                    final Predicate<? super T> filter) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(t, collection, mapper, filter));
        }

        boolean result;

        if (filter.test(t)) {
            final T mappedValue = mapper.apply(t);

            result = collection.add(mappedValue);
        } else {
            result = true;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Inserts the element into the collection after applying the
     * mapper function if the element is not already present.
     *
     * @param   t           T
     * @param   collection  java.util.Collection&lt;? super T&gt;
     * @param   mapper      java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return              boolean
     */
    protected boolean applyAndAdd(final T t,
                               final Collection<? super T> collection,
                               final Function<? super T, ? extends T> mapper) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(t, collection, mapper));
        }

        final T mappedValue = mapper.apply(t);
        final boolean result = collection.add(mappedValue);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }

    /**
     * Adds all the elements in the specified collection to this list.
     * Apply the mapper function to each element before adding it.
     *
     * @param   target  java.util.Collection&lt;? super T&gt;
     * @param   source  java.util.Collection&lt;? extends T&gt;
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    protected boolean applyAndAddAll(final Collection<? super T> target,
                                     final Collection<? extends T> source,
                                     final Function<? super T, ? extends T> mapper) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(target, source, mapper));
        }

        final WrappedObject<Boolean> result = WrappedObject.of(false);

        if (!source.isEmpty()) {
            source.forEach(e -> {
                result.set(target.add(mapper.apply(e)));
            });
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result.get()));
        }

        return result.get();
    }

    /**
     * Apply the onElement to each element
     * and then clear the collection.
     *
     * @param   collection  java.util.Collection&lt;? extends T&gt;
     * @param   onElement   java.util.function.Consumer&lt;? super T&gt;
     * @param   onEnd       java.lang.Runnable
     */
    protected void clearAndApply(final Collection<? extends T> collection,
                                 final Consumer<? super T> onElement,
                                 final Runnable onEnd) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(collection, onElement, onEnd));
        }

        collection.forEach(e -> {
            if (e != null) {
                this.runTask(() -> onElement.accept(e));
            }
        });

        collection.clear();

        onEnd.run();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Consume all the elements in the collection.
     *
     * @param   collection  java.util.Collection&lt;? extends T&gt;
     * @param   onElement   java.util.function.Consumer&lt;? super T&gt;
     * @param   onEnd       java.lang.Runnable
     */
    protected void consume(final Collection<? extends T> collection,
                           final Consumer<? super T> onElement,
                           final Runnable onEnd) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(collection, onElement, onEnd));
        }

        collection.forEach(e -> {
            if (e != null) {
                this.runTask(() -> onElement.accept(e));
            }
        });

        onEnd.run();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Removes all of this collection's elements that are also contained in the specified
     * collection (optional operation). After this call returns, this collection will contain
     * no elements in common with the specified collection.
     * Apply the onElement consumer to each removed element.
     *
     * @param   target      java.util.Collection&lt;? super T&gt;
     * @param   source      java.util.Collection&lt;? extends T&gt;
     * @param   onElement   java.util.function.Consumer&lt;T&gt;
     * @param   onEnd       java.lang.Runnable
     * @return              boolean
     */
    protected boolean removeAllAndApply(final Collection<? super T> target,
                                        final Collection<? extends T> source,
                                        final Consumer<T> onElement,
                                        final Runnable onEnd) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(target, source, onElement, onEnd));
        }

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

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result.get()));
        }

        return result.get();
    }
    /**
     * Retains only the elements in this list that are contained
     * in the specified collection (optional operation). In other
     * words, removes from this list all of its elements that are
     * not contained in the specified collection. After this call
     * returns, this collection will contain only elements in common
     * with the specified collection.
     * Apply the onElement consumer to each retained element.
     *
     * @param   target      java.util.Collection&lt;T&gt;
     * @param   source      java.util.Collection&lt;? extends T&gt;
     * @param   onElement   java.util.function.Consumer&lt;T&gt;
     * @return              boolean
     */
    protected boolean retainAllAndApply(final Collection<T> target,
                                        final Collection<? extends T> source,
                                        final Consumer<T> onElement,
                                        final Runnable onEnd) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(target, source, onElement, onEnd));
        }

        final WrappedObject<Boolean> result = WrappedObject.of(false);
        final List<T> removals = new ArrayList<>();

        for (final T element : target) {
            if (source.contains(element)) {
                this.runTask(() -> onElement.accept(element));
            } else {
                removals.add(element);
            }
        }

        if (!removals.isEmpty()) {
            target.removeAll(removals);
            result.set(true);
        }

        onEnd.run();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result.get()));
        }

        return result.get();
    }

    /**
     * Run the task by submitting the
     * runnable to the executor service.
     *
     * @param   task    java.lang.Runnable
     */
    protected void runTask(final Runnable task) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(task));
        }

        this.futures.add(this.executor.submit(task));

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Wait for the consumers to finish.
     */
    protected void waitForConsumers() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        this.waitForFutures();

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
                } catch (InterruptedException | ExecutionException e) {
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
}
