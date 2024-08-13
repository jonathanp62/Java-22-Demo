package net.jmp.demo.java22.util;

/*
 * (#)TestAppliedQueue.java 0.5.0   08/13/2024
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicBoolean;

import java.util.function.Function;

import static org.awaitility.Awaitility.await;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class TestAppliedQueue {
    public static final int AWAIT_TIME = 500;

    @Test
    public void testApplyAndOffer() {
        final AppliedQueue<Integer> queue = new AppliedQueue<>();
        final Function<Integer, Integer> function = x -> x + 1;

        queue.applyAndOffer(1, function);
        queue.applyAndOffer(2, function);
        queue.applyAndOffer(3, function);

        assertEquals(3, queue.size());

        assertTrue(queue.contains(2));
        assertTrue(queue.contains(3));
        assertTrue(queue.contains(4));
    }

    @Test
    public void testApplyAndAdd() {
        final AppliedQueue<Integer> queue = new AppliedQueue<>();
        final Function<Integer, Integer> function = x -> x + 1;

        queue.applyAndOffer(1, function);
        queue.applyAndOffer(2, function);
        queue.applyAndOffer(3, function);

        assertEquals(3, queue.size());

        assertTrue(queue.contains(2));
        assertTrue(queue.contains(3));
        assertTrue(queue.contains(4));
    }

    @Test
    public void testElementAndApply() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

        queue.offer("value");

        queue.start();

        final String value = queue.elementAndApply(e -> {
            System.out.println(STR."testElementAndApply: \{e}");
            consumerSwitch.set(true);
        });

        assertEquals("value", value);
        assertEquals(1, queue.size());

        await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                .untilAsserted(
                        () -> assertThat(consumerSwitch.get())
                                .isTrue()
                );

        queue.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testElementAndApplyWhenNotStarted() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final var _ = queue.elementAndApply(System.out::println);
    }

    @Test(expected = NoSuchElementException.class)
    public void testElementAndApplyOnEmptyQueue() {
        final AppliedQueue<String> queue = new AppliedQueue<>();

        queue.start();

        final var _ = queue.elementAndApply(System.out::println);
    }

    @Test
    public void testPeekAndApply() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

        queue.offer("value");

        queue.start();

        final String value = queue.peekAndApply(e -> {
            System.out.println(STR."testPeekAndApply: \{e}");
            consumerSwitch.set(true);
        });

        assertEquals("value", value);
        assertEquals(1, queue.size());

        await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                .untilAsserted(
                        () -> assertThat(consumerSwitch.get())
                                .isTrue()
                );

        queue.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testPeekAndApplyWhenNotStarted() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final var _ = queue.peekAndApply(System.out::println);
    }

    @Test
    public void testPollAndApply() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

        queue.offer("value");

        queue.start();

        final String value = queue.pollAndApply(e -> {
            System.out.println(STR."testPollAndApply: \{e}");
            consumerSwitch.set(true);
        });

        assertEquals("value", value);
        assertEquals(0, queue.size());

        await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                .untilAsserted(
                        () -> assertThat(consumerSwitch.get())
                                .isTrue()
                );

        queue.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testPollAndApplyWhenNotStarted() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final var _ = queue.pollAndApply(System.out::println);
    }

    @Test
    public void testRemoveAndApply() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

        queue.offer("value");

        queue.start();

        final String value = queue.removeAndApply(e -> {
            System.out.println(STR."testRemoveAndApply: \{e}");
            consumerSwitch.set(true);
        });

        assertEquals("value", value);
        assertEquals(0, queue.size());

        await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                .untilAsserted(
                        () -> assertThat(consumerSwitch.get())
                                .isTrue()
                );

        queue.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAndApplyWhenNotStarted() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final var _ = queue.pollAndApply(System.out::println);
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveAndApplyOnEmptyQueue() {
        final AppliedQueue<String> queue = new AppliedQueue<>();

        queue.start();

        final var _ = queue.removeAndApply(System.out::println);
    }

    @Test
    public void testRemoveAllAndApply() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final AtomicBoolean consumerSwitch = new AtomicBoolean(false);
        final List<String> values = List.of("value 1", "value 2", "value 3");

        values.forEach(queue::offer);

        queue.start();

        final boolean result = queue.removeAllAndApply(values, e -> {
            System.out.println(STR."testRemoveAllAndApply: \{e}");
            consumerSwitch.set(true);
        });

        assertTrue(result);
        assertEquals(0, queue.size());

        await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                .untilAsserted(
                        () -> assertThat(consumerSwitch.get())
                                .isTrue()
                );

        queue.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAllAndApplyWhenNotStarted() {
        final AppliedQueue<String> queue = new AppliedQueue<>();
        final List<String> list = new ArrayList<>();
        final var _ = queue.removeAllAndApply(list, System.out::println);
    }
}
