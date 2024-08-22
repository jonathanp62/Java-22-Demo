package net.jmp.demo.java22.demos;

/*
 * (#)AppliedCollectionDemo.java    0.7.1   08/22/2024
 * (#)AppliedCollectionDemo.java    0.7.0   08/19/2024
 * (#)AppliedCollectionDemo.java    0.5.0   08/10/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.1
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

import java.util.function.Consumer;
import java.util.function.Function;

import java.util.stream.IntStream;

import net.jmp.demo.java22.util.AppliedList;
import net.jmp.demo.java22.util.AppliedQueue;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A demonstration class for applied collections.
 */
public final class AppliedCollectionDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** A string capitalizer function. */
    private final Function<String, String> capitalizer = string -> {
        final String firstLetter = string.substring(0, 1).toUpperCase();

        return firstLetter + string.substring(1);
    };

    /**
     * The default constructor.
     */
    public AppliedCollectionDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.appliedQueues();
        this.appliedLists();

        this.logger.exit();
    }

    /**
     * Applied queues.
     */
    private void appliedQueues() {
        this.logger.entry();

        this.offerToAndPollFromQueue();
        this.peekAtAndRemoveFromQueue();
        this.addToAndRemoveAllFromQueue();

        this.logger.exit();
    }

    /**
     * Use offer and poll on an applied string queue.
     */
    private void offerToAndPollFromQueue() {
        this.logger.entry();

        try (final AppliedQueue<String> stringQueue = new AppliedQueue<>()) {
            final List<String> words = List.of(
                    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
            );

            words.forEach(word -> {
                if (!stringQueue.applyAndOffer(word, this.capitalizer)) {
                    this.logger.warn("Failed to offer word: {}", word);
                }
            });

            while (!stringQueue.isEmpty()) {
                final String word = stringQueue.pollAndApply(e -> {
                    this.logger.info(STR."QE: \{e.toUpperCase()})");
                });

                this.logger.info("The polled word: {}", word);
            }

            assert stringQueue.isEmpty();
        }

        this.logger.exit();
    }

    /**
     * Use peek and remove on an applied integer queue.
     */
    private void peekAtAndRemoveFromQueue() {
        this.logger.entry();

        try (final AppliedQueue<Integer> integerQueue = new AppliedQueue<>()) {
            var _ = integerQueue.applyAndAdd(1, i -> i * 10);
            var _ = integerQueue.applyAndAdd(2, i -> i * 11);
            var _ = integerQueue.applyAndAdd(3, i -> i * 12);
            var _ = integerQueue.applyAndAdd(4, i -> i * 13);
            var _ = integerQueue.applyAndAdd(5, i -> i * 14);

            while (integerQueue.peekAndApply(e -> this.logger.info("Peeked: {}", e)) != null) {
                final int _ = integerQueue.removeAndApply(e -> this.logger.info("Removed: {}", e));
            }

            assert integerQueue.isEmpty();
        }

        this.logger.exit();
    }

    /**
     * Use add and remove all on an applied integer queue.
     */
    private void addToAndRemoveAllFromQueue() {
        this.logger.entry();

        try (final AppliedQueue<Integer> integerQueue = new AppliedQueue<>()) {
            IntStream.rangeClosed(1, 10).forEach(i -> integerQueue.applyAndAdd(i, j -> j * 10));

            final var odds = List.of(10, 30, 50, 70, 90);

            if (integerQueue.removeAllAndApply(odds, e -> this.logger.info("Removed: {}", e), () -> {})) {
                this.logger.info("Odd numbers removed");
            }

            if (!integerQueue.removeAllAndApply(odds, e -> this.logger.info("Removed: {}", e), () -> {})) {
                this.logger.info("Odd numbers were not removed");
            }
        }

        this.logger.exit();
    }

    /**
     * Applied lists.
     */
    private void appliedLists() {
        this.logger.entry();

        this.addToAndRemoveFromList();

        this.logger.exit();
    }

    /**
     * Add to and remove from a list.
     */
    private void addToAndRemoveFromList() {
        this.logger.entry();

        try (final AppliedList<String> stringList = new AppliedList<>()) {
            final List<String> words = List.of(
                    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
            );

            words.forEach(word -> {
                if (!stringList.applyAndAdd(word, this.capitalizer)) {
                    this.logger.warn("Failed to add word: {}", word);
                }
            });

            final Consumer<String> consumer = string -> this.logger.info(STR."LE: \{string.toUpperCase()})");

            stringList.removeAndApply("One", consumer);
            stringList.removeIfAndApply("Two", s -> s.length() == 3, consumer);
            stringList.removeIfAndApply("Three", s -> s.length() == 5, consumer);
            stringList.removeIfAndApply("Ten", s -> s.length() == 3, consumer);
            stringList.removeIfAndApply("Four", s -> s.length() == 4, consumer);
            stringList.removeIfAndApply("Five", s -> s.length() == 4, consumer);
            stringList.removeIfAndApply("Six", s -> s.length() == 3, consumer);
            stringList.removeIfAndApply("Seven", s -> s.length() == 5, consumer);
            stringList.removeAndApply("Eight", consumer);
            stringList.removeAndApply("Nine", consumer);

            assert stringList.isEmpty();
        }

        this.logger.exit();
    }
}
