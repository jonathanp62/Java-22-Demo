package net.jmp.demo.java22;

/*
 * (#)BeforeSuperDemo.java  0.3.0   08/07/2024
 *
 * @author   Jonathan Parker
 * @version  0.3.0
 * @since    0.3.0
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

import java.util.Objects;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * A class the demonstrates statements before 'super()' in a constructor.
 * This allows developers to place essential initialization logic before
 * invoking the superclass constructor. Additionally, this can be used to
 * transform values received in the derived class before calling the base
 * class constructor. Note that statements we put before super() cannot
 * access instance variables or execute methods and access to "this" is
 * prohibited.
 */
final class BeforeSuperDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    BeforeSuperDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        final var square = new Square(4, 3, "Yellow");

        this.logger.info(square.toString());

        try {
            new Square(4, 0, "Orange");
        } catch (final IllegalArgumentException iae) {
            this.logger.error(iae.getMessage());
        }

        this.logger.exit();
    }

    /**
     * A shape.
     */
    static class Shape {
        protected String color;

        /**
         * The constructor.
         *
         * @param   color   java.lang.String
         */
        public Shape(final String color) {
            super();

            this.color = Objects.requireNonNull(color);
        }
    }

    /**
     * A square.
     */
    static final class Square extends Shape {
        private final int sides;
        private final int length;

        /**
         * The constructor.
         *
         * @param   sides   int
         * @param   length  int
         */
        Square(final int sides, final int length, final String color) {
            if (sides != 4) {
                throw new IllegalArgumentException("Squares must have four sides");
            }

            if (length <= 0) {
                throw new IllegalArgumentException("Length must be greater than 0");
            }

            super(color);

            this.sides = sides;
            this.length = length;
        }

        /**
         * The to-string method.
         *
         * @return  java.lang.String
         */
        @Override
        public String toString() {
            return STR."Square {sides=\{this.sides}, length=\{this.length}, color=\"\{this.color}\"";
        }
    }
}
