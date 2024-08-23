package net.jmp.demo.java22.util;

/*
 * (#)TestLoggerUtils.java  0.8.0   08/22/2024
 *
 * @author   Jonathan Parker
 * @version  0.8.0
 * @since    0.8.0
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

import static net.jmp.demo.java22.constants.LoggerConstants.*;

import static net.jmp.demo.java22.util.LoggerUtils.*;

import static org.junit.Assert.*;

import net.jmp.demo.java22.constants.LoggerConstants;
import org.junit.Test;

public final class TestLoggerUtils {
    @Test
    public void testEntry() {
        final String result = entry();

        assert result != null;
        assertEquals(ENTRY, result);
    }

    @Test
    public void testEntryWith() {
        String result = entryWith(true);

        assert result != null;
        assertEquals("entry with (true)", result);

        result = entryWith(123, true);

        assert result != null;
        assertEquals("entry with (123, true)", result);

        result = entryWith(123, "some string", true);

        assert result != null;
        assertEquals("entry with (123, some string, true)", result);
    }

    @Test
    public void testExit() {
        final String result = exit();

        assert result != null;
        assertEquals(EXIT, result);
    }

    @Test
    public void testExitWith() {
        String result = exitWith(true);

        assert result != null;
        assertEquals("exit with (true)", result);

        result = exitWith("some string");

        assert result != null;
        assertEquals("exit with (some string)", result);

        result = exitWith(123);

        assert result != null;
        assertEquals("exit with (123)", result);
    }
}
