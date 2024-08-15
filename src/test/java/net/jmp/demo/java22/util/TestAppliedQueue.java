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

import static org.junit.Assert.*;

import org.junit.Test;

public final class TestAppliedQueue {
    public static final int AWAIT_TIME = 500;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructWithZeroThreads() {
        try (final var _ = new AppliedQueue<Integer>(0)) {
            assertTrue(true);
        }
    }

    @Test
    public void testApplyAndOffer() {
        try (final AppliedQueue<Integer> queue = new AppliedQueue<>()) {
            final Function<Integer, Integer> function = x -> x + 1;

            queue.applyAndOffer(1, function);
            queue.applyAndOffer(2, function);
            queue.applyAndOffer(3, function);

            assertEquals(3, queue.size());

            assertTrue(queue.contains(2));
            assertTrue(queue.contains(3));
            assertTrue(queue.contains(4));
        }
    }

    @Test
    public void testApplyAndAdd() {
        try (final AppliedQueue<Integer> queue = new AppliedQueue<>()) {
            final Function<Integer, Integer> function = x -> x + 1;

            queue.applyAndOffer(1, function);
            queue.applyAndOffer(2, function);
            queue.applyAndOffer(3, function);

            assertEquals(3, queue.size());

            assertTrue(queue.contains(2));
            assertTrue(queue.contains(3));
            assertTrue(queue.contains(4));
        }
    }

    @Test
    public void testElementAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value");

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
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testElementAndApplyOnEmptyQueue() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final var _ = queue.elementAndApply(System.out::println);
        }
    }

    @Test
    public void testPeekAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value");

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
        }
    }

    @Test
    public void testPollAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value");

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
        }
    }

    @Test
    public void testRemoveAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value");

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
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveAndApplyOnEmptyQueue() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final var _ = queue.removeAndApply(System.out::println);
        }
    }

    @Test
    public void testRemoveAllAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);
            final List<String> values = List.of("value 1", "value 2", "value 3");

            values.forEach(queue::offer);

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
        }
    }

    @Test
    public void testRemoveAllAndApplyOnEmptyCollection() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            values.forEach(queue::offer);

            final boolean result = queue.removeAllAndApply(new ArrayList<>(), e -> {
                System.out.println(STR."testRemoveAllAndApply: \{e}");
            });

            assertFalse(result);
            assertEquals(3, queue.size());
        }
    }

    @Test
    public void testRemoveAllAndApplyOnNonMatchingCollection() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");
            final List<String> removals = List.of("value 4", "value 5");

            values.forEach(queue::offer);

            final boolean result = queue.removeAllAndApply(removals, e -> {
                System.out.println(STR."testRemoveAllAndApply: \{e}");
            });

            assertFalse(result);
            assertEquals(3, queue.size());
        }
    }

    @Test
    public void testApplyAndAddAll() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            final boolean result = queue.applyAndAddAll(values, e -> e.toUpperCase());

            assertTrue(result);
            assertEquals(3, queue.size());
            assertTrue(queue.contains("VALUE 1"));
            assertTrue(queue.contains("VALUE 2"));
            assertTrue(queue.contains("VALUE 3"));
        }
    }

    @Test
    public void testApplyAndAddAllOnEmptyCollection() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final boolean result = queue.applyAndAddAll(new ArrayList<>(), x -> x);

            assertFalse(result);
            assertEquals(0, queue.size());
        }
    }

    @Test
    public void testRemoveIfAndApply() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value 1");
            queue.offer("value 2");
            queue.offer("value 3");

            final boolean result = queue.removeIfAndApply(
                    x -> x.startsWith("value"),
                    e -> {
                        System.out.println(STR."testRemoveIfAndApply: \{e}");
                        consumerSwitch.set(true);
                    }
            );

            assertTrue(result);
            assertEquals(0, queue.size());

            await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                    .untilAsserted(
                            () -> assertThat(consumerSwitch.get())
                                    .isTrue()
                    );
        }
    }

    @Test
    public void testRemoveIfAndApplyNoPredicateMatches() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final AtomicBoolean consumerSwitch = new AtomicBoolean(false);

            queue.offer("value 1");
            queue.offer("value 2");
            queue.offer("value 3");

            final boolean result = queue.removeIfAndApply(
                    x -> x.endsWith("value"),
                    e -> {
                        System.out.println(STR."testRemoveIfAndApply: \{e}");
                        consumerSwitch.set(true);
                    }
            );

            assertFalse(result);
            assertEquals(3, queue.size());
        }
    }

    @Test
    public void testRemoveIfAndApplyOnEmptyQueue() {
        try (final AppliedQueue<String> queue = new AppliedQueue<>()) {
            final var result = queue.removeIfAndApply(String::isEmpty, System.out::println);

            assertFalse(result);
        }
    }
}
