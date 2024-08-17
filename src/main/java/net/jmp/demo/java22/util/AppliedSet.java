package net.jmp.demo.java22.util;

/*
 * (#)AppliedSet.java   0.6.0   08/17/2024
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

import java.util.*;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

public final class AppliedSet<T> extends AppliedBaseCollection<T> implements Set<T>, AutoCloseable {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** The set. */
    private Set<T> set;

    /**
     * The default constructor.
     */
    public AppliedSet() {
        super();

        this.set = new HashSet<>();
    }

    /**
     * A constructor that takes
     * the number of threads to use.
     */
    public AppliedSet(final int numThreads) {
        super(numThreads);

        this.set = new HashSet<>();
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

    /*
     * Methods to implement:
     *   addIf
     *   applyAndAdd
     *   applyAndAddIf
     *   clearAndApply
     *   removeAllAndApply
     *   removeIf
     *   removeIfAndApply
     *   retainAllAndApply
     */

    /* Set and Collection method overrides */

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.set.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.set.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.set.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.set.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.set.removeAll(c);
    }

    @Override
    public void clear() {
        this.set.clear();
    }
}
