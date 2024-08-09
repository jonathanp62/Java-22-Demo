package net.jmp.demo.java22.gatherers;

/*
 * (#)MaxByGatherer.java    0.4.0   08/09/2024
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

import java.util.Map;
import java.util.Objects;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import java.util.stream.Gatherer;

/**
 * This gatherer designed to find the maximum element in a stream based on a selector function.
 *
 * @param   <T> The type of input elements to the gathering operation
 * @param   <C> A type that extends Comparable; T must extend Comparable
 */
public final class MaxByGatherer<T, C extends Comparable<C>> implements Gatherer<T, MaxByGathererState<T>, T> {
    private final Function<T, C> selector;

    /**
     * The constructor.
     *
     * @param selector java.util.function.Function&lt;T, A&gt;
     */
    public MaxByGatherer(final Function<T, C> selector) {
        this.selector = Objects.requireNonNull(selector);
    }

    /**
     * A function that produces an instance of the intermediate
     * state used for this gathering operation.
     *
     * @return java.util.function.Supplier&lt;java.util.Map&lt;A, T&gt;&gt;
     */
    @Override
    public Supplier<MaxByGathererState<T>> initializer() {
        return MaxByGathererState::new;
    }

    /**
     * A function which integrates provided elements,
     * potentially using the provided intermediate state,
     * optionally producing output to the provided
     * downstream type.
     *
     * @return java.util.stream.Gatherer.Integrator&lt;net.jmp.demo.java22.gatherers.MaxByGathererState&lt;T&gt;, T, T&gt;
     */
    @Override
    public Integrator<MaxByGathererState<T>, T, T> integrator() {
        /*
         * Greedy integrators consume all their input,
         * and may only relay that the downstream does
         * not want more elements. The greedy lambda is
         * the state (A), the element type (T), and the
         * result type (R).
         */

        return Integrator.ofGreedy((state, item, _) -> {
            if (state.maxElement == null) {
                state.maxElement = item;

                return true;    // True if subsequent integration is desired
            }

            final C currentItem = selector.apply(item);
            final C maxItem = selector.apply(state.maxElement);

            if (currentItem.compareTo(maxItem) > 0) {
                state.maxElement = item;
            }

            return true;    // True if subsequent integration is desired
        });
    }

    /**
     * A function which accepts two intermediate states and combines them into one.
     * Used for parallel streams to combine states from different segments.
     *
     * @return  java.util.function.BinaryOperator&lt;net.jmp.demo.java22.gatherers.MaxByGathererState&lt;T&gt;&gt;
     */
    @Override
    public BinaryOperator<MaxByGathererState<T>> combiner() {
        /*
         * A BinaryOperator represents an operation upon two
         * operands of the same type, producing a result of
         * the same type as the operands.
         */

        return (first, second) -> {
            // Check for nulls

            if (first.maxElement == null && second.maxElement == null) {
                return null;
            }

            if (first.maxElement == null) {
                return second;
            }

            if (second.maxElement == null) {
                return first;
            }

            final C firstItem = selector.apply(first.maxElement);
            final C secondItem = selector.apply(second.maxElement);

            if (firstItem.compareTo(secondItem) > 0) {
                return first;
            } else {
                return second;
            }
        };
    }

    /**
     * A function which accepts the final intermediate state and a
     * downstream object, allowing to perform a final action at the
     * end of input elements. The lambda is the state (A) and the
     * result type (R).
     *
     * @return  java.util.function.BiConsumer&lt;net.jmp.demo.java22.gatherers.MaxByGathererState&lt;T&gt;, java.util.stream.Gatherer.Downstream&gt;
     */
    @Override
    public BiConsumer<MaxByGathererState<T>, Downstream<? super T>> finisher () {
        return (state, downstream) -> downstream.push(state.maxElement);
    }
}