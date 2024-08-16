package net.jmp.demo.java22.util;

/*
 * (#)WrappedObject.java    0.6.0   08/15/2024
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

import java.util.Objects;

/**
 * A wrapper for objects. Created initially to address the issue
 * of needing to change the state of a non-final variable from
 * a lambda.
 *
 * @param   <T> The type of object to wrap
 */
public final class WrappedObject<T> {
    /** The object. */
    private T object;

    /**
     * The default constructor.
     */
    public WrappedObject() {
        super();
    }

    /**
     * A constructor that takes the object.
     *
     * @param   object  T
     */
    public WrappedObject(final T object) {
        super();

        this.object = Objects.requireNonNull(object, () -> "T 'object' is null");
    }

    /**
     * Set the object.
     *
     * @param   object  T
     */
    public void set(final T object) {
        this.object = Objects.requireNonNull(object, () -> "T 'object' is null");
    }

    /**
     * Return the object.
     *
     * @return  T
     */
    public T get() {
        return this.object;
    }
}
