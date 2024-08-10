package net.jmp.demo.java22.gatherers;

/*
 * (#)FindLastGatherer.java 0.4.0   08/10/2024
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

import java.util.*;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import java.util.stream.Gatherer;

/**
 * This gatherer filters out items based on a predicate function and returns the last.
 * The optional combiner operation is not present in this gatherer.
 *
 * @param   <T> The type of input elements to the gathering operation
 */
public final class FindLastGatherer<T>  implements Gatherer<T, List<T>, T> {
    /** The predicate function. */
    private final Predicate<T> predicate;

    /**
     * The constructor.
     *
     * @param   predicate   java.util.function.Predicate&lt;T&gt;
     */
    public FindLastGatherer(final Predicate<T> predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }

    /**
     * A function that produces an instance of the intermediate
     * state used for this gathering operation.
     *
     * @return  java.util.function.Supplier&lt;java.util.List&lt;A&gt;&gt;
     */
    @Override
    public Supplier<List<T>> initializer() {
        return ArrayList::new;
    }

    /**
     * A function which integrates provided elements,
     * potentially using the provided intermediate state,
     * optionally producing output to the provided
     * downstream type.
     *
     * @return  java.util.stream.Gatherer.Integrator&lt;java.util.List&lt;T&gt;, T, T&gt;
     */
    @Override
    public Integrator<List<T>, T, T> integrator() {
        /*
         * Greedy integrators consume all their input,
         * and may only relay that the downstream does
         * not want more elements. The greedy lambda is
         * the state (A), the element type (T), and the
         * result type (R).
         */

        return Integrator.ofGreedy((state, item, _) -> {
            if (this.predicate.test(item)) {
                state.add(item);
            }

            return true;    // True if subsequent integration is desired
        });
    }

    /**
     * A function which accepts the final intermediate state and a
     * downstream object, allowing to perform a final action at the
     * end of input elements. The lambda is the state (A) and the
     * result type (R).
     *
     * @return  java.util.function.BiConsumer&lt;java.util.List&lt;T&gt;, java.util.stream.Gatherer.Downstream&gt;
     */
    @Override
    public BiConsumer<List<T>, Downstream<? super T>> finisher () {
        return (state, downstream) -> {
            final int count = state.size();
            final T lastItem = state.get(count - 1);

            downstream.push(lastItem);
        };
    }
}
