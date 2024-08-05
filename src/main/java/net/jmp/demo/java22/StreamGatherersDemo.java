package net.jmp.demo.java22;

/*
 * (#)StreamGatherersDemo.java  0.2.0   08/04/2024
 *
 * @author   Jonathan Parker
 * @version  0.2.0
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

import java.util.stream.Gatherers;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * The class that demonstrates built-in stream gatherers.
 */
final class StreamGatherersDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    StreamGatherersDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.slidingWindows();
        this.fixedWindows();
        this.scan();
        this.fold();
        this.mapConcurrent();

        this.logger.exit();
    }

    /**
     * Sliding windows.
     */
    private void slidingWindows() {
        this.logger.entry();

        final List<String> countries = List.of("India", "Poland", "UK", "Australia", "USA", "Netherlands");

        final List<List<String>> windows = countries
                .stream()
                .gather(Gatherers.windowSliding(3))
                .toList();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Sliding windows: {}", windows);
        }

        this.logger.exit();
    }

    /**
     * Fixed windows.
     */
    private void fixedWindows() {
        this.logger.entry();

        final List<String> composers = List.of("Mozart", "Bach", "Beethoven", "Mahler", "Bruckner", "Liszt", "Chopin", "Telemann", "Vivaldi");

        final List<List<String>> windows = composers
                .stream()
                .gather(Gatherers.windowFixed(2))
                .toList();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Fixed windows: {}", windows);
        }

        this.logger.exit();
    }

    /**
     * Scan.
     */
    private void scan() {
        this.logger.entry();

        final List<String> numbers = Stream.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9
        ).gather(
                Gatherers.scan(() -> "", (string, number) -> string + number)
        ).toList();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Scan: {}", numbers);
        }

        this.logger.exit();
    }

    /**
     * Fold.
     */
    private void fold() {
        this.logger.entry();
        this.logger.exit();
    }

    /**
     * Map concurrent.
     */
    private void mapConcurrent() {
        this.logger.entry();
        this.logger.exit();
    }
}
