package net.jmp.demo.java22.util;

/*
 * (#)TestAppliedList.java  0.6.0   08/17/2024
 *
 * @author   Jonathan Parker
 * @version  0.6.0
 * @since    0.6.0
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

import java.util.concurrent.TimeUnit;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.IntStream;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static org.awaitility.Awaitility.await;

import static org.junit.Assert.*;

public final class TestAppliedList {
    public static final int AWAIT_TIME = 500;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructWithZeroThreads() {
        try (final var _ = new AppliedList<Integer>(0)) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddIf() {
        try (final AppliedList<Integer> list = new AppliedList<>()) {
            final Predicate<Integer> isEven = i -> i % 2 == 0;

            IntStream.rangeClosed(1, 6)
                    .forEach(i -> {
                        if (!list.addIf(i, isEven))
                            fail(STR."Failed to add element \{i}");
                    });

            assertFalse(list.isEmpty());
            assertEquals(3, list.size());
            assertTrue(list.contains(2));
            assertTrue(list.contains(4));
            assertTrue(list.contains(6));
        }
    }

    @Test
    public void testApplyAndAddIf() {
        try (final AppliedList<Integer> list = new AppliedList<>()) {
            final Function<Integer, Integer> timesTwo = x -> x * 2;
            final Predicate<Integer> isOdd = i -> i % 2 != 0;

            IntStream.rangeClosed(1, 6)
                    .forEach(i -> {
                        if (!list.applyAndAddIf(i, timesTwo, isOdd))
                            fail(STR."Failed to add element \{i}");
                    });

            assertFalse(list.isEmpty());
            assertEquals(3, list.size());
            assertTrue(list.contains(2));
            assertTrue(list.contains(6));
            assertTrue(list.contains(10));
        }
    }

    @Test
    public void testApplyAndAdd() {
        try (final AppliedList<Integer> list = new AppliedList<>()) {
            final Function<Integer, Integer> function = x -> x + 1;

            assertTrue(list.applyAndAdd(1, function));
            assertTrue(list.applyAndAdd(2, function));
            assertTrue(list.applyAndAdd(3, function));

            assertEquals(3, list.size());

            assertTrue(list.contains(2));
            assertTrue(list.contains(3));
            assertTrue(list.contains(4));
        }
    }

    @Test
    public void testApplyAndAddAll() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            final boolean result = list.applyAndAddAll(values, String::toUpperCase);

            assertTrue(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("VALUE 1"));
            assertTrue(list.contains("VALUE 2"));
            assertTrue(list.contains("VALUE 3"));
        }
    }

    @Test
    public void testApplyAndAddAllOnEmptyCollection() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final boolean result = list.applyAndAddAll(new ArrayList<>(), x -> x);

            assertFalse(result);
            assertEquals(0, list.size());
        }
    }

    @Test
    public void testRemoveAndApplyByObjectFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final WrappedObject<String> removedElement = new WrappedObject<>();

            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final Consumer<String> consumer = e -> {
                removedElement.set(e.toUpperCase());
                consumed.set(true);
            };

            final boolean result = list.removeAndApply("value 2", consumer);

            assertTrue(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 3"));

            await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                    .untilAsserted(
                            () -> assertThat(consumed.get())
                                    .isTrue()
                    );

            assertEquals("VALUE 2", removedElement.get());
        }
    }

    @Test
    public void testRemoveAndApplyByObjectNotFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final boolean result = list.removeAndApply("value 4", System.out::println);

            assertFalse(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveAndApplyByNullObject() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final boolean result = list.removeAndApply(null, System.out::println);

            assertFalse(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains(null));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveAndApplyByIndexFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final WrappedObject<String> removedElement = new WrappedObject<>();

            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final Consumer<String> consumer = e -> {
                removedElement.set(e.toUpperCase());
                consumed.set(true);
            };

            final String result = list.removeAndApply(1, consumer);

            assertNotNull(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 3"));

            await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                    .untilAsserted(
                            () -> assertThat(consumed.get())
                                    .isTrue()
                    );

            assertEquals("VALUE 2", removedElement.get());
        }
    }

    @Test
    public void testRemoveAndApplyByIndexedNull() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final String result = list.removeAndApply(1, System.out::println);

            assertNull(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveAndApplyByIndexNotFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final String _ = list.removeAndApply(3, System.out::println);
        }
    }

    @Test
    public void testRemoveIfAndApplyByObjectFound() {

    }

    @Test
    public void testRemoveIfAndApplyByObjectFoundNoMatch() {

    }

    @Test
    public void testRemoveIfAndApplyByObjectNotFound() {

    }

    @Test
    public void testRemoveIfAndApplyByNullObject() {

    }

    @Test
    public void testRemoveIfAndApplyByIndexFound() {

    }

    @Test
    public void testRemoveIfAndApplyByIndexFoundNoMatch() {

    }

    @Test
    public void testRemoveIfAndApplyByIndexedNull() {

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIfAndApplyByIndexNotFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final String _ = list.removeIfAndApply(3, String::isEmpty, System.out::println);
        }
    }
}
