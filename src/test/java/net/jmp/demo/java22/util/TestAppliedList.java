package net.jmp.demo.java22.util;

/*
 * (#)TestAppliedList.java  0.7.1   08/22/2024
 * (#)TestAppliedList.java  0.7.0   08/19/2024
 * (#)TestAppliedList.java  0.6.0   08/17/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.1
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
import java.util.Objects;

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
    public void testOfEmpty() {
        try (final AppliedList<Integer> list = AppliedList.of()) {
            assertTrue(list.isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> list.add(0));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testOfNull() {
        final AppliedList<Integer> _ = AppliedList.of(null);
    }

    @Test
    public void testOfOne() {
        try (final AppliedList<Integer> list = AppliedList.of(1)) {
            assertFalse(list.isEmpty());
            assertEquals(1, list.size());
            assertTrue(list.contains(1));
            assertThrows(UnsupportedOperationException.class, () -> list.add(0));
        }
    }

    @Test
    public void testOfTwo() {
        try (final AppliedList<Integer> list = AppliedList.of(1, 2)) {
            assertFalse(list.isEmpty());
            assertEquals(2, list.size());
            assertTrue(list.contains(1));
            assertTrue(list.contains(2));
            assertThrows(UnsupportedOperationException.class, () -> list.add(0));
        }
    }

    @Test
    public void testOfThree() {
        try (final AppliedList<Integer> list = AppliedList.of(1, 2, 3)) {
            assertFalse(list.isEmpty());
            assertEquals(3, list.size());
            assertTrue(list.contains(1));
            assertTrue(list.contains(2));
            assertTrue(list.contains(3));
            assertThrows(UnsupportedOperationException.class, () -> list.add(0));
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
    public void testClearAndApply() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));

            list.clearAndApply(e -> System.out.println(STR."Cleared: \{e}"), () -> consumed.set(true));

            list.waitForConsumers();

            assertTrue(consumed.get());
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsume() {
        final List<String> results = new ArrayList<>();

        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));

            list.consume(results::add, () -> {});
        }

        assertFalse(results.isEmpty());
        assertEquals(3, results.size());
        assertTrue(results.contains("value 1"));
        assertTrue(results.contains("value 2"));
        assertTrue(results.contains("value 3"));
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

            assertTrue(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
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
    public void testRemoveAllAndApply() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = new WrappedObject<>(false);
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final boolean result = list.removeAllAndApply(
                    values,
                    e -> System.out.println(STR."testRemoveAllAndApply: \{e}"),
                    () -> consumed.set(true)
            );

            await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                    .untilAsserted(
                            () -> assertThat(consumed.get())
                                    .isTrue()
                    );

            assertTrue(result);
            assertEquals(0, list.size());
        }
    }

    @Test
    public void testRemoveAllAndApplyOnEmptyCollection() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            values.forEach(list::add);

            final boolean result = list.removeAllAndApply(new ArrayList<>(), e -> {
                System.out.println(STR."testRemoveAllAndApply: \{e}");
            }, () -> {});

            assertFalse(result);
            assertEquals(3, list.size());
        }
    }

    @Test
    public void testRemoveAllAndApplyOnNonMatchingCollection() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");
            final List<String> removals = List.of("value 4", "value 5");

            values.forEach(list::add);

            final boolean result = list.removeAllAndApply(
                    removals,
                    e -> System.out.println(STR."testRemoveAllAndApply: \{e}"),
                    () -> {}
            );

            assertFalse(result);
            assertEquals(3, list.size());
        }
    }

    @Test
    public void testRemoveIfAndApplyByObjectFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final WrappedObject<String> removedElement = new WrappedObject<>();

            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final Consumer<String> consumer = e -> {
                removedElement.set(e.toUpperCase());
                consumed.set(true);
            };

            final boolean result = list.removeIfAndApply("value 2", s -> s.startsWith("value"), consumer);

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
    public void testRemoveIfAndApplyByObjectFoundNoMatch() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final boolean result = list.removeIfAndApply("value 2", s -> s.isEmpty(), System.out::println);

            assertFalse(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByObjectNotFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final boolean result = list.removeIfAndApply("value 4", s -> s.startsWith("value"), System.out::println);

            assertFalse(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByNullObject() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final boolean result = list.removeIfAndApply(null, Objects::isNull, System.out::println);

            assertTrue(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByNullObjectNoMatch() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final boolean result = list.removeIfAndApply(null, Objects::nonNull, System.out::println);

            assertFalse(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains(null));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByIndexFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final WrappedObject<String> removedElement = new WrappedObject<>();

            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final Consumer<String> consumer = e -> {
                removedElement.set(e.toUpperCase());
                consumed.set(true);
            };

            final String result = list.removeIfAndApply(1, s -> s.startsWith("value"), consumer);

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
    public void testRemoveIfAndApplyByIndexFoundNoMatch() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final String result = list.removeIfAndApply(1, String::isEmpty, System.out::println);

            assertNull(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 2"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByIndexedNull() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final String result = list.removeIfAndApply(1, Objects::isNull, System.out::println);

            assertNull(result);
            assertEquals(2, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test
    public void testRemoveIfAndApplyByIndexedNullNoMatch() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            list.add("value 1");
            list.add(null);
            list.add("value 3");

            final String result = list.removeIfAndApply(1, Objects::nonNull, System.out::println);

            assertNull(result);
            assertEquals(3, list.size());
            assertTrue(list.contains("value 1"));
            assertTrue(list.contains(null));
            assertTrue(list.contains("value 3"));
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIfAndApplyByIndexNotFound() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final String _ = list.removeIfAndApply(3, String::isEmpty, System.out::println);
        }
    }

    /* @todo This is a flaky unit test */

    @Test
    public void testRetainAllAndApplyOnFullMatchingCollection() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final List<String> values = List.of("value 1", "value 2", "value 3");
            final List<String> results = new ArrayList<>();

            list.addAll(values);

            final boolean result = list.retainAllAndApply(values, results::add, () -> consumed.set(true));

            await().atMost(AWAIT_TIME, TimeUnit.MILLISECONDS)
                    .untilAsserted(
                            () -> assertThat(results.size())
                                    .isEqualTo(3)
                    );

            assertFalse(result);
            assertTrue(consumed.get());
            assertEquals(3, results.size());
            assertTrue(results.contains("value 1"));
            assertTrue(results.contains("value 2"));
            assertTrue(results.contains("value 3"));
        }
    }

    @Test
    public void testRetainAllAndApplyOnPartialMatchingCollection() {
        try (final AppliedList<String> list = new AppliedList<>()) {
            final WrappedObject<Boolean> consumed = WrappedObject.of(false);
            final List<String> values = List.of("value 1", "value 2", "value 3");
            final List<String> retains = List.of("value 1", "value 3");
            final List<String> results = new ArrayList<>();

            list.addAll(values);

            final boolean result = list.retainAllAndApply(retains, results::add, () -> consumed.set(true));

            assertTrue(result);

            list.waitForConsumers();

            assertTrue(consumed.get());
            assertEquals(2, results.size());
            assertTrue(results.contains("value 1"));
            assertTrue(results.contains("value 3"));
        }
    }

    @Test
    public void testRetainAllAndApplyOnEmptyCollection() {
        final List<String> results = new ArrayList<>();

        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");

            list.addAll(values);

            final boolean result = list.retainAllAndApply(new ArrayList<>(), results::add, () -> {});

            assertTrue(result);
            assertEquals(0, list.size());
        }

        assertEquals(0, results.size());
    }

    @Test
    public void testRetainAllAndApplyOnNonMatchingCollection() {
        final List<String> results = new ArrayList<>();

        try (final AppliedList<String> list = new AppliedList<>()) {
            final List<String> values = List.of("value 1", "value 2", "value 3");
            final List<String> retains = List.of("value 4", "value 5");

            list.addAll(values);

            final boolean result = list.retainAllAndApply(retains, results::add, () -> {});

            assertTrue(result);
        }

        assertEquals(0, results.size());
    }
}
