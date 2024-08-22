package net.jmp.demo.java22.util;

/*
 * (#)LoggerUtils.java  0.7.1   08/22/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.1
 * @since    0.7.1
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

import static net.jmp.demo.java22.constants.LoggerConstants.ENTRY;
import static net.jmp.demo.java22.constants.LoggerConstants.EXIT;

/**
 * Logger utilities to assist in creating
 * trace entry and exit records.
 */
public final class LoggerUtils {
    /**
     * The default constructor.
     */
    private LoggerUtils() {
        super();
    }

    /**
     * Format a trace entry message.
     *
     * @return  java.lang.String
     */
    public static String entry() {
        return STR."\{ENTRY}";
    }

    /**
     * Format a trace entry message with arguments.
     *
     * @param   objArray    java.lang.Object[]
     * @return              java.lang.String
     */
    public static String entryWith(final Object... objArray) {
        final StringBuilder sb = new StringBuilder();

        sb.append(STR."\{ENTRY} with (");

        for (int i = 0; i < objArray.length; i++) {
            sb.append(objArray[i]);

            if (i != objArray.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    /**
     * Format a trace exit message.
     *
     * @return  java.lang.String
     */
    public static String exit() {
        return STR."\{EXIT}";
    }

    /**
     * Format a trace exit message with an argument.
     *
     * @param   object  java.lang.Object[]
     * @return          java.lang.String
     */
    public static String exitWith(final Object object) {
        return STR."\{EXIT} with (\{object.toString()})";
    }
}
