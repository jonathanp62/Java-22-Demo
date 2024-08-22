package net.jmp.demo.java22.demos;

/*
 * (#)KeyedFunctionExecutorDemo.java    0.7.1   08/22/2024
 * (#)KeyedFunctionExecutorDemo.java    0.5.0   08/14/2024
 * (#)KeyedFunctionExecutorDemo.java    0.2.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.1
 * @since    0.2.0
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

import java.util.List;

import java.util.function.Function;

import java.util.stream.IntStream;

import net.jmp.demo.java22.util.KeyedFunctionExecutor;

import static net.jmp.demo.java22.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that demonstrates the keyed function executor.
 */
public final class KeyedFunctionExecutorDemo implements Demo {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * The default constructor.
     */
    public KeyedFunctionExecutorDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        try (final KeyedFunctionExecutor<String> keyedFunctionExecutor = new KeyedFunctionExecutor<>()) {
            final Function<String, Void> function = s -> {
                logger.info("Function processed value: {}", s);

                return null;
            };

            final List<String> elements =
                    IntStream.rangeClosed(1, 50)
                            .mapToObj(i -> STR."Item \{i}")
                            .toList();

            elements.forEach(e -> {
                keyedFunctionExecutor.process(function, e, STR."Value for \{e}");
            });
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
