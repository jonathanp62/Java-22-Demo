package net.jmp.demo.java22;

/*
 * (#)UnnamedVariablesDemo.java 0.6.0   08/15/2024
 * (#)UnnamedVariablesDemo.java 0.3.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.6.0
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

import java.util.*;

import net.jmp.demo.java22.util.AppliedQueue;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

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
        this.unnamedPatterns();

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

        // Try with resources

        try (var _ = new AppliedQueue<String>()) {
            this.logger.info("Opened an applied queue");
        }

        this.logger.exit();
    }

    /**
     * Unnamed patterns.
     */
    private void unnamedPatterns() {
        this.logger.entry();

        // Patterns in case statements

        final List<String> list = new ArrayList<>();
        final Set<String> set = new HashSet<>();
        final Collection<String> collection = set;

        switch (collection) {
            case Set<String> _ -> this.logger.info("Set");
            case List<String> _ -> this.logger.info("List");
            default -> this.logger.error("Unsupported collection");
        }

        this.logger.exit();
    }
}
