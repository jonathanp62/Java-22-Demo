package net.jmp.demo.java22.gatherers;

/*
 * (#)FindFirstGatherer.java    0.4.0   08/10/2024
 *
 * @author   Jonathan Parker
 * @version  0.4.0
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

import java.util.Objects;

import java.util.function.Predicate;

import java.util.stream.Gatherer;

/**
 * This gatherer filters out items based on a predicate function and returns the first.
 * The optional initializer operation is not present in this gatherer.
 * The optional combiner operation is not present in this gatherer.
 * The optional finisher operation is not present in this gatherer.
 *
 * @param   <T> The type of input elements to the gathering operation
 */
public final class FindFirstGatherer<T> implements Gatherer<T, T, T> {
    /** The predicate function. */
    private final Predicate<T> predicate;

    /**
     * The constructor.
     *
     * @param   predicate   java.util.function.Predicate&lt;T&gt;
     */
    public FindFirstGatherer(final Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }

    /**
     * A function which integrates provided elements,
     * potentially using the provided intermediate state,
     * optionally producing output to the provided
     * downstream type.
     *
     * @return  java.util.stream.Gatherer.Integrator&lt;T, T, T&gt;
     */
    @Override
    public Integrator<T, T, T> integrator() {
        /*
         * Greedy integrators consume all their input,
         * and may only relay that the downstream does
         * not want more elements. The greedy lambda is
         * the state (A), the element type (T), and the
         * result type (R).
         */

        return Integrator.ofGreedy((_, item, downstream) -> {
            if (this.predicate.test(item)) {
                downstream.push(item);

                return false;   // No subsequent integration is desired
            } else {
                return true;    // True if subsequent integration is desired
            }
        });
    }
}
