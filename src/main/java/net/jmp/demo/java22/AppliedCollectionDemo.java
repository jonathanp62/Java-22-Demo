package net.jmp.demo.java22;

/*
 * (#)AppliedCollectionDemo.java    0.5.0   08/10/2024
 *
 * @author   Jonathan Parker
 * @version  0.5.0
 * @since    0.5.0
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

import net.jmp.demo.java22.util.AppliedQueue;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A demonstration class for applied collections.
 */
final class AppliedCollectionDemo implements Demo {
    /**
     * The logger.
     */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    AppliedCollectionDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.appliedQueues();

        this.logger.exit();
    }

    /**
     * Applied queues.
     */
    private void appliedQueues() {
        this.logger.entry();

        this.appliedStringQueue();
        this.appliedIntegerQueue();

        this.logger.exit();
    }

    /**
     * An applied string queue.
     */
    private void appliedStringQueue() {
        this.logger.entry();

        final AppliedQueue<String> stringQueue = new AppliedQueue<>();

        final Function<String, String> capitalizer = string -> {
            final String firstLetter = string.substring(0, 1).toUpperCase();

            return firstLetter + string.substring(1);
        };

        final List<String> words = List.of("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten");

        words.forEach(word -> {
            if (!stringQueue.offerAndApply(word, capitalizer)) {
                this.logger.warn("Failed to offer word: {}", word);
            }
        });

        stringQueue.start();

        while (!stringQueue.isEmpty()) {
            final String word = stringQueue.pollAndApply(e -> {
                this.logger.info(STR."QE: \{e.toUpperCase()})");
            });

            this.logger.info("The polled word: {}", word);
        }

        stringQueue.stop();

        assert stringQueue.isEmpty();

        this.logger.exit();
    }

    /**
     * An applied integer queue.
     */
    private void appliedIntegerQueue() {
        this.logger.entry();

        final AppliedQueue<Integer> integerQueue = new AppliedQueue<>();

        var _ = integerQueue.addAndApply(1, i -> i * 10);
        var _ = integerQueue.addAndApply(2, i -> i * 11);
        var _ = integerQueue.addAndApply(3, i -> i * 12);
        var _ = integerQueue.addAndApply(4, i -> i * 13);
        var _ = integerQueue.addAndApply(5, i -> i * 14);

        integerQueue.start();

        while (integerQueue.peekAndApply(e -> this.logger.info("Peeked: {}", e)) != null) {
            final int _ = integerQueue.removeAndApply(e -> this.logger.info("Removed: {}", e));
        }

        integerQueue.stop();

        assert integerQueue.isEmpty();

        this.logger.exit();
    }
}
