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

import java.util.function.Function;

import net.jmp.demo.java22.util.AppliedQueue;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

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

        this.appliedQueue();

        this.logger.exit();
    }

    /**
     * Applied queue.
     */
    private void appliedQueue() {
        this.logger.entry();

        final AppliedQueue<String> stringQueue = new AppliedQueue<>();

        stringQueue.offer("one");
        stringQueue.offer("two");
        stringQueue.offer("three");
        stringQueue.offer("four");
        stringQueue.offer("five");
        stringQueue.offer("six");
        stringQueue.offer("seven");
        stringQueue.offer("eight");
        stringQueue.offer("nine");
        stringQueue.offer("ten");

        stringQueue.start();

        stringQueue.apply(e -> {
            this.logger.info(STR."QE: \{e.toUpperCase()})");

            return null;
        });

        stringQueue.stop();

        final AppliedQueue<Integer> integerQueue = new AppliedQueue<>();

        final Function<Integer, Integer> timesTwo = e -> {
            final int i = e * 2;

            this.logger.info(STR."QE: \{i}");

            return i;
        };

        integerQueue.offer(1);
        integerQueue.offer(2);
        integerQueue.offer(3);
        integerQueue.offer(4);
        integerQueue.offer(5);

        integerQueue.start();
        integerQueue.apply(timesTwo);
        integerQueue.stop();

        this.logger.exit();
    }
}
