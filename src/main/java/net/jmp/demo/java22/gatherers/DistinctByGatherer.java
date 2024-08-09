package net.jmp.demo.java22.gatherers;

/*
 * (#)DistinctByGatherer.java   0.4.0   08/09/2024
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import java.util.function.Function;
import java.util.function.Supplier;

import java.util.stream.Gatherer;

/**
 * This gatherer ensures stream elements are unique based on a selector function.
 * The optional combiner operation is not present in this gatherer.
 * The optional finisher operation is not present in this gatherer.
 *
 * @param   <T> The type of input elements to the gathering operation
 * @param   <A> The potentially mutable state type of the gathering operation
 */
public final class DistinctByGatherer<T, A> implements Gatherer<T, Set<A>, T> {
    /** The selector function. */
    private final Function<T, A> selector;

    /**
     * The constructor.
     *
     * @param   selector    java.util.function.Function&lt;T, A&gt;
     */
    public DistinctByGatherer(final Function<T, A> selector) {
        this.selector = Objects.requireNonNull(selector);
    }

    /**
     * A function that produces an instance of the intermediate
     * state used for this gathering operation.
     *
     * @return  java.util.function.Supplier&lt;java.util.Set&lt;A&gt;&gt;
     */
    @Override
    public Supplier<Set<A>> initializer() {
        return HashSet::new;
    }

    /**
     * A function which integrates provided elements,
     * potentially using the provided intermediate state,
     * optionally producing output to the provided
     * downstream type.
     *
     * @return  java.util.stream.Gatherer.Integrator&lt;java.util.Set&lt;A&gt;, T, T&gt;
     */
    @Override
    public Integrator<Set<A>, T, T> integrator() {
        /*
         * Greedy integrators consume all their input,
         * and may only relay that the downstream does
         * not want more elements. The greedy lambda is
         * the state (A), the element type (T), and the
         * result type (R).
         */

        return Integrator.ofGreedy((state, item, downstream) -> {
            final A selected = this.selector.apply(item);   // Apply the selector function

            if (!state.contains(selected)) {
                state.add(selected);

                if (!downstream.push(item)) {
                    System.err.println(STR."Failed to push \{item} downstream");

                    return false;   // No subsequent integration is desired
                }
            }

            return true;    // True if subsequent integration is desired
        });
    }
}
