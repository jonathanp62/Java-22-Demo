package net.jmp.demo.java22.gatherers;

/*
 * (#)ReduceByGatherer.java 0.4.0   08/09/2024
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
import java.util.function.BiFunction;
import java.util.function.Function;

import java.util.function.Supplier;

import java.util.stream.Gatherer;

/**
 * This gatherer aggregates elements in a stream based on a selector function.
 *
 * @param   <T> The type of input elements to the gathering operation
 * @param   <A> The potentially mutable state type of the gathering operation
 */
public final class ReduceByGatherer<T, A> implements Gatherer<T, Map<A, T>, T>  {
    /** The selector function. */
    private final Function<T, A> selector;

    /** The reducer function. */
    private final BiFunction<T, T, T> reducer;

    /**
     * The constructor.
     *
     * @param   selector    java.util.function.Function&lt;T, A&gt;
     * @param   reducer     java.util.function.BiFunction&lt;T, T, T&gt;
     */
    public ReduceByGatherer(final Function<T, A> selector, final BiFunction<T, T, T> reducer) {
        this.selector = Objects.requireNonNull(selector);
        this.reducer = Objects.requireNonNull(reducer);
    }

    /**
     * A function that produces an instance of the intermediate
     * state used for this gathering operation.
     *
     * @return  java.util.function.Supplier&lt;java.util.Map&lt;A, T&gt;&gt;
     */
    @Override
    public Supplier<Map<A, T>> initializer() {
        return HashMap::new;
    }

    /**
     * A function which integrates provided elements,
     * potentially using the provided intermediate state,
     * optionally producing output to the provided
     * downstream type.
     *
     * @return  java.util.stream.Gatherer.Integrator&lt;java.util.Map&lt;A, T&gt;, T, T&gt;
     */

    @Override
    public Integrator<Map<A, T>, T, T> integrator() {
        /*
         * Greedy integrators consume all their input,
         * and may only relay that the downstream does
         * not want more elements. The greedy lambda is
         * the state (A), the element type (T), and the
         * result type (R).
         */

        return Integrator.ofGreedy((state, item, _) -> {
            state.merge(this.selector.apply(item), item, this.reducer);

            return true;    // True if subsequent integration is desired
        });
    }

    /**
     * A function which accepts the final intermediate state and a
     * downstream object, allowing to perform a final action at the
     * end of input elements. The lambda is the state (A) and the
     * result type (R).
     *
     * @return  java.util.function.BiConsumer&lt;java.util.Map&lt;A, T&gt;, java.util.stream.Gatherer.Downstream&gt;
     */
    @Override
    public BiConsumer<Map<A, T>, Downstream<? super T>> finisher () {
        return (state, downstream) -> {
            state.values()
                    .forEach(downstream::push);
        };
    }
}
