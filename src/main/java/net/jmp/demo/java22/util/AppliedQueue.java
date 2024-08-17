package net.jmp.demo.java22.util;

/*
 * (#)AppliedQueue.java 0.6.0   08/16/2024
 * (#)AppliedQueue.java 0.5.0   08/10/2024
 * (#)AppliedQueue.java 0.4.0   08/09/2024
 *
 * @author   Jonathan Parker
 * @version  0.6.0
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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * An applied queue.
 *
 * @param   <T> The type of element
 */
public final class AppliedQueue<T> extends AppliedBaseCollection<T> implements Queue<T>, AutoCloseable {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** The queue. */
    private final Queue<T> queue;

    /**
     * The default constructor.
     */
    public AppliedQueue() {
        super();

        this.queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    public AppliedQueue(final int numThreads) {
        super(numThreads);

        this.queue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Close any resources.
     */
    @Override
    public void close() {
        this.logger.entry();

        super.close();

        this.logger.exit();
    }

    /**
     * Inserts the element into the queue if the
     * applied predicate function evaluates to true.
     *
     * @param   t       T
     * @param   filter  java.util.function.Predicate&lt;? super T&gt;
     * @return          boolean
     */
    public boolean addIf(final T t, @Nonnull final Predicate<? super T> filter) {
        this.logger.entry(t, filter);

        boolean result = true;  // Return true if the element was filtered out

        if (filter.test(t)) {
            result = this.queue.add(t);
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the queue after applying
     * the mapper function if the applied predicate
     * function evaluates to true.
     *
     * @param   t       T
     * @param   mapper  java.util.function.Function&lt;? super T,? extends T&gt;
     * @param   filter  java.util.function.Predicate&lt;? super T&gt;
     * @return          boolean
     */
    public boolean applyAndAddIf(final T t, final Function<? super T, ? extends T> mapper, @Nonnull final Predicate<? super T> filter) {
        this.logger.entry(t, mapper, filter);

        boolean result = true;  // Return true if the element was filtered out

        if (filter.test(t)) {
            final T mappedValue = mapper.apply(t);

            result = this.queue.add(mappedValue);
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the queue if the
     * applied predicate function evaluates to true.
     *
     * @param   t       T
     * @param   filter  java.util.function.Predicate&lt;? super T&gt;
     * @return          boolean
     */
    public boolean offerIf(final T t, @Nonnull final Predicate<? super T> filter) {
        this.logger.entry(t, filter);

        boolean result = true;  // Return true if the element was filtered out

        if (filter.test(t)) {
            result = this.queue.offer(t);
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the queue after applying
     * the mapper function if the applied predicate
     * function evaluates to true.
     *
     * @param   t       T
     * @param   mapper  java.util.function.Function&lt;? super T,? extends T&gt;
     * @param   filter  java.util.function.Predicate&lt;? super T&gt;
     * @return          boolean
     */
    public boolean applyAndOfferIf(final T t, final Function<? super T, ? extends T> mapper, @Nonnull final Predicate<? super T> filter) {
        this.logger.entry(t, mapper, filter);

        boolean result = true;  // Return true if the element was filtered out

        if (filter.test(t)) {
            final T mappedValue = mapper.apply(t);

            result = this.queue.offer(mappedValue);
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the queue after applying the mapper function.
     *
     * @param   t       T
     * @param   mapper  java.util.function.Function&lt;? super T,? extends T&gt;
     * @return          boolean
     */
    public boolean applyAndOffer(final T t, final Function<? super T, ? extends T> mapper) {
        this.logger.entry(t, mapper);

        final T mappedValue = mapper.apply(t);
        final boolean result = this.queue.offer(mappedValue);

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the queue after applying the mapper function.
     *
     * @param   t       T
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    public boolean applyAndAdd(final T t, final Function<? super T, ? extends T> mapper) {
        this.logger.entry(t, mapper);

        final T mappedValue = mapper.apply(t);
        final boolean result = this.queue.add(mappedValue);

        this.logger.exit(result);

        return result;
    }

    /**
     * Adds all the elements in the specified collection to this queue.
     * Apply the mapper function to each element before adding it.
     *
     * @param   c       java.util.Collection&lt;? extends T&gt;
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    public boolean applyAndAddAll(@Nonnull final Collection<? extends T> c, final Function<? super T, ? extends T> mapper) {
        this.logger.entry(c, mapper);

        final WrappedObject<Boolean> result = new WrappedObject<>(false);

        if (!c.isEmpty()) {
            c.forEach(e -> {
                this.queue.add(mapper.apply(e));
                result.set(true);
            });
        }

        this.logger.exit(result.get());

        return result.get();
    }

    /**
     * Retrieves, but does not remove, the head of this queue. This method differs
     * from peekAndApply only in that it throws an exception if this queue is empty.
     * Apply the consumer to the retrieved element.
     *
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              T
     */
    public T elementAndApply(final Consumer<T> consumer) throws NoSuchElementException {
        this.logger.entry(consumer);

        if (this.queue.isEmpty()) {
            throw new NoSuchElementException();
        }

        final T element = this.queue.element();

        super.runTask(() -> consumer.accept(element));

        this.logger.exit(element);

        return element;
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if
     * this queue is empty.
     * Apply the consumer to the retrieved element if it is not null.
     *
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              T
     */
    public T peekAndApply(final Consumer<T> consumer) {
        this.logger.entry(consumer);

        final T element = this.queue.peek();

        if (element != null) {
            super.runTask(() -> consumer.accept(element));
        }

        this.logger.exit(element);

        return element;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * Apply the consumer to the retrieved element if it is not null.
     *
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     */
    public T pollAndApply(final Consumer<T> consumer) {
        this.logger.entry(consumer);

        final T element = this.queue.poll();

        if (element != null) {
            super.runTask(() -> consumer.accept(element));
        }

        this.logger.exit(element);

        return element;
    }

    /**
     * Retrieves and removes the head of this queue, or throw an exception if this queue is empty.
     * Apply the consumer to the retrieved element if it is not null.
     *
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     */
    public T removeAndApply(final Consumer<T> consumer) throws NoSuchElementException {
        this.logger.entry(consumer);

        if (this.queue.isEmpty()) {
            throw new NoSuchElementException();
        }

        final T element = this.queue.remove();

        if (element != null) {
            super.runTask(() -> consumer.accept(element));
        }

        this.logger.exit(element);

        return element;
    }

    /**
     * Removes all of this collection's elements that are also contained in the specified
     * collection (optional operation). After this call returns, this collection will contain
     * no elements in common with the specified collection.
     * Apply the consumer to each removed element.
     *
     * @param   c           java.util.Collection&lt;? extends T&gt;
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              boolean
     */
    public boolean removeAllAndApply(@Nonnull final Collection<? extends T> c, final Consumer<T> consumer) {
        this.logger.entry(c);

        final WrappedObject<Boolean> result = new WrappedObject<>(false);

        if (!c.isEmpty()) {
            c.forEach(e -> {
                if (this.queue.contains(e) && this.queue.remove(e)) {
                    super.runTask(() -> consumer.accept(e));
                    result.set(true);
                }
            });
        }

        this.logger.exit(result.get());

        return result.get();
    }

    /**
     * Removes all the elements of this collection that satisfy the given predicate.
     *
     * @param   filter      java.util.function.Predicate&lt;? super T&gt;
     * @param   consumer    java.util.function.Consumer&lt;&gt;
     * @return              boolean
     */
    public boolean removeIfAndApply(@Nonnull final Predicate<? super T> filter, @Nonnull final Consumer<T> consumer) {
        this.logger.entry(filter, consumer);

        final WrappedObject<Boolean> result = new WrappedObject<>(false);

        if (!this.queue.isEmpty()) {
            this.queue.forEach(e -> {
                if (this.queue.removeIf(filter)) {
                    super.runTask(() -> consumer.accept(e));
                    result.set(true);
                }
            });
        }

        this.logger.exit(result.get());

        return result.get();
    }

    /* Queue and Collection method overrides */

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.queue.contains(o);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return this.queue.iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return this.queue.toArray();
    }

    @Override
    @Nonnull
    public <T1> T1[] toArray(@Nonnull T1[] a) {
        return this.queue.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.queue.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return this.queue.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> c) {
        return this.queue.addAll(c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return this.queue.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return this.queue.retainAll(c);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public boolean offer(T t) {
        return this.queue.offer(t);
    }

    @Override
    public T remove() {
        return this.queue.remove();
    }

    @Override
    public T poll() {
        return this.queue.poll();
    }

    @Override
    public T element() {
        return this.queue.element();
    }

    @Override
    public T peek() {
        return this.queue.peek();
    }
}
