package net.jmp.demo.java22.util;

/*
 * (#)AppliedList.java  0.7.0   08/18/2024
 * (#)AppliedList.java  0.6.0   08/17/2024
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

import java.util.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * An applied list.
 *
 * @param   <T> The type of element
 */
public final class AppliedList<T> extends AppliedBaseCollection<T> implements List<T>, AutoCloseable {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** The list. */
    private final List<T> list;

    /**
     * The default constructor.
     */
    public AppliedList() {
        super();

        this.list = new ArrayList<>();
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    public AppliedList(final int numThreads) {
        super(numThreads);

        this.list = new ArrayList<>();
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
     * Inserts the element into the list if the
     * applied predicate function evaluates to true.
     *
     * @param   t       T
     * @param   filter  java.util.function.Predicate&lt;? super T&gt;
     * @return          boolean
     */
    public boolean addIf(final T t, @Nonnull final Predicate<? super T> filter) {
        this.logger.entry(t, filter);

        boolean result;

        if (filter.test(t)) {
            result = this.list.add(t);
        } else {
            result = true;
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the list after applying
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

        boolean result;

        if (filter.test(t)) {
            final T mappedValue = mapper.apply(t);

            result = this.list.add(mappedValue);
        } else {
            result = true;
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Inserts the element into the list after applying the mapper function.
     *
     * @param   t       T
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    public boolean applyAndAdd(final T t, final Function<? super T, ? extends T> mapper) {
        this.logger.entry(t, mapper);

        final T mappedValue = mapper.apply(t);
        final boolean result = this.list.add(mappedValue);

        this.logger.exit(result);

        return result;
    }

    /**
     * Adds all the elements in the specified collection to this list.
     * Apply the mapper function to each element before adding it.
     *
     * @param   c       java.util.Collection&lt;? extends T&gt;
     * @param   mapper  java.util.function.Function&lt;? super T, ? extends T&gt;
     * @return          boolean
     */
    public boolean applyAndAddAll(@Nonnull final Collection<? extends T> c, final Function<? super T, ? extends T> mapper) {
        this.logger.entry(c, mapper);

        final WrappedObject<Boolean> result = WrappedObject.of(false);

        if (!c.isEmpty()) {
            c.forEach(e -> {
                this.list.add(mapper.apply(e));
                result.set(true);
            });
        }

        this.logger.exit(result.get());

        return result.get();
    }

    /**
     * Removes the first occurrence of this element from the list if one exists.
     * Apply the consumer to the removed element if it is not null.
     *
     * @param   object      T
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              boolean
     */
    public boolean removeAndApply(final T object, final Consumer<T> consumer) {
        this.logger.entry(object, consumer);

        final int index = this.list.indexOf(object);

        boolean result = false;

        if (index >= 0) {
            final T element = this.list.get(index);

            if (element != null) {
                result = this.list.remove(element);

                super.runTask(() -> consumer.accept(element));
            }
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Removes the element at the indexed position from the list.
     * Apply the consumer to the removed element if it is not null.
     *
     * @param   index       int
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              T
     */
    public T removeAndApply(final int index, final Consumer<T> consumer) {
        this.logger.entry(index, consumer);

        final T element = this.list.remove(index);

        if (element != null) {
            super.runTask(() -> consumer.accept(element));
        }

        this.logger.exit(element);

        return element;
    }

    /**
     * Removes the first occurrence of this element from the list if one exists
     * and the applied predicate function evaluates to true.
     * Apply the consumer to the removed element if it is not null.
     *
     * @param   object      T
     * @param   matcher     java.util.function.Predicate&lt;? super T&gt;
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              boolean
     */
    public boolean removeIfAndApply(final T object, final Predicate<? super T> matcher, final Consumer<T> consumer) {
        this.logger.entry(object, matcher, consumer);

        final int index = this.list.indexOf(object);

        boolean result = false;

        if (index >= 0) {
            final T element = this.list.get(index);

            if (element != null && matcher.test(element)) {
                result = this.list.remove(element);

                super.runTask(() -> consumer.accept(element));
            }
        }

        this.logger.exit(result);

        return result;
    }

    /**
     * Removes the element at the indexed position from the list if
     * the applied predicate function evaluates to true.
     * Apply the consumer to the removed element if it is not null.
     *
     * @param   index       int
     * @param   matcher     java.util.function.Predicate&lt;? super T&gt;
     * @param   consumer    java.util.function.Consumer&lt;T&gt;
     * @return              T
     */
    public T removeIfAndApply(final int index, final Predicate<? super T> matcher, final Consumer<T> consumer) {
        this.logger.entry(index, matcher, consumer);

        final T element = this.list.remove(index);

        if (element != null && matcher.test(element)) {
            super.runTask(() -> consumer.accept(element));
        }

        this.logger.exit(element);

        return element;
    }

    /*
     * Implement methods:
     *   clearAndApply
     *   removeAllAndApply
     *   retainAllAndApply
     */

    /* List and Collection method overrides */

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.stream().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public T get(int index) {
        return this.list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
