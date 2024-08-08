package net.jmp.demo.java22;

/*
 * (#)UnnamedVariablesDemo.java 0.3.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.3.0
 * @since    0.3.0
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

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

\
/**
 * A class the demonstrates using unnamed variables.
 */
final class UnnamedVariablesDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    UnnamedVariablesDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.unnamedVariables();

        this.logger.exit();
    }

    /**
     * Unnamed variables.
     */
    private void unnamedVariables() {
        this.logger.entry();

        // As an exception

        try {
            int number = 10 / 0;
        } catch (ArithmeticException _) {
            this.logger.error("Division by zero");
        }

        final var items = List.of(1, 2, 3, 4, 5);

        int totalItems = 0;

        // In an enhanced for loop

        for (final int _ : items) {
            totalItems++;
        }

        assert totalItems == items.size();

        // As an unneeded item

        for (int i = 0; i < items.size(); i++) {
            final var _ = items.get(i);
        }

        // In a lambda

        items.forEach(_ -> this.logger.info("An iteration"));

        this.logger.exit();
    }

    /**
     * Unnamed patterns.
     */
    private void unnamedPatterns() {
        this.logger.entry();
        this.logger.exit();
    }
}
