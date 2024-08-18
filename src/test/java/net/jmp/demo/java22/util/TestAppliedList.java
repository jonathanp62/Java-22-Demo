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

import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.IntStream;

import org.junit.Test;

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
}
