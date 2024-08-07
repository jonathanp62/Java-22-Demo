package net.jmp.demo.java22;

/*
 * (#)StringTemplatesDemo.java  0.2.0   08/06/2024
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

import java.io.File;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import static java.lang.StringTemplate.RAW;

import static java.util.FormatProcessor.FMT;

import java.util.List;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * The class that demonstrates string templates.
 */
final class StringTemplatesDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /**
     * The default constructor.
     */
    StringTemplatesDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        final List<Demo> demos = List.of(
                new STRTemplateProcessor(),
                new FMTTemplateProcessor()
        );

        demos.forEach(Demo::demo);

        this.raw();
        this.interpolation();

        this.logger.exit();
    }

    /**
     * RAW is a standard template processor that
     * produces an unprocessed StringTemplate object.
     */
    private void raw() {
        this.logger.entry();

        final String name = "Jonathan";
        final StringTemplate st = RAW."My name is \{name}";
        final String info = STR.process(st);

        assert info.equals("My name is Jonathan");

        this.logger.info(info);

        this.logger.exit();
    }

    /**
     * RAW fragments and values and
     * interpolating them.
     */
    private void interpolation() {
        this.logger.entry();

        final int x = 10;
        final int y = 20;

        final StringTemplate st = RAW."\{x} + \{y} = \{x + y}";

        final List<String> fragments = st.fragments();
        final List<Object> values = st.values();

        this.logger.info(st.toString());

        fragments.forEach(fragment -> this.logger.info("Fragment: {}", fragment));
        values.forEach(fragment -> this.logger.info("Value   : {}", fragment));

        final String string = StringTemplate.interpolate(fragments, values);

        assert string.equals("10 + 20 = 30");

        this.logger.info(string);

        this.logger.exit();
    }

    /**
     * Demonstrate the STR template processor.
     */
    class STRTemplateProcessor implements Demo {
        @Override
        public void demo() {
            logger.entry();

            this.strings();
            this.arithmetic();
            this.invokeMethodsAndAccessFields();
            this.unescapedDoubleQuotes();
            this.multilineExpressions();
            this.leftToRightEvaluation();
            this.nestedExpressions();

            logger.exit();
        }

        /**
         * Embedded expressions can be strings
         */
        private void strings() {
            logger.entry();

            final String firstName = "Bill";
            final String lastName  = "Duck";
            final String fullName  = STR."\{firstName} \{lastName}";

            assert fullName.equals("Bill Duck");

            logger.info(fullName);

            final String sortedName  = STR."\{lastName}, \{firstName}";

            assert sortedName.equals("Duck, Bill");

            logger.info(sortedName);

            logger.exit();
        }

        /**
         * Embedded expressions can perform arithmetic
         */
        private void arithmetic() {
            logger.entry();

            final int x = 10, y = 20;
            final String s = STR."\{x} + \{y} = \{x + y}";

            assert s.equals("10 + 20 = 30");

            logger.info(s);

            logger.exit();
        }

        /**
         * Embedded expressions can invoke methods and access fields.
         */
        private void invokeMethodsAndAccessFields() {
            logger.entry();

            final String s = STR."You have a \{getOfferType()} waiting for you!";

            assert s.equals("You have a gift waiting for you!");

            logger.info(s);

            final Request request = new Request("2022-03-25", "15:34", "8.8.8.8");
            final String t = STR."Access at \{request.date} \{request.time} from \{request.ipAddress}";

            assert t.equals("Access at 2022-03-25 15:34 from 8.8.8.8");

            logger.info(t);

            logger.exit();
        }

        /**
         * Unescaped double quotes. Shows more
         * how an expression can be evaluated.
         */
        private void unescapedDoubleQuotes() {
            logger.entry();

            final String filePath = "tmp.dat";
            final File file = new File(filePath);
            final String message = STR."The file \{filePath} \{file.exists() ? "does" : "does not"} exist";

            assert message.equals("The file tmp.dat does not exist");

            logger.info(message);

            logger.exit();
        }

        /**
         * Spread an expression over multiple lines.
         */
        private void multilineExpressions() {
            logger.entry();

            final String time = STR."The time is \{
                    // The java.time.format package is very useful
                    DateTimeFormatter
                            .ofPattern("HH:mm:ss")
                            .format(LocalTime.now())
                    } right now";

            logger.info(time);

            final String name    = "Joan Smith";
            final String phone   = "555-123-4567";
            final String address = "1 Maple Drive, Anytown";
            final String json = STR."""

                {
                    "name":    "\{name}",
                    "phone":   "\{phone}",
                    "address": "\{address}"
                }
                """;

            logger.info(json);

            logger.exit();
        }

        /**
         * Left to right evaluation.
         */
        private void leftToRightEvaluation() {
            logger.entry();

            int index = 0;

            final String data = STR."\{index++}, \{index++}, \{index++}, \{index++}";

            assert data.equals("0, 1, 2, 3");

            logger.info(data);

            logger.exit();
        }

        /**
         * Nesting template expressions.
         */
        private void nestedExpressions() {
            logger.entry();

            final String[] fruit = { "apples", "oranges", "peaches" };

            final String s1 = STR."\{fruit[0]}, \{STR."\{fruit[1]}, \{fruit[2]}"}";

            final String s2 = STR."\{fruit[0]}, \{
                    STR."\{fruit[1]}, \{fruit[2]}"
                    }";

            assert s1.equals("apples, oranges, peaches");
            assert s2.equals(s1);

            logger.info(s2);

            logger.exit();
        }

        /**
         * Return an offer type.
         *
         * @return  java.lang.String
         */
        private String getOfferType() {
            return "gift";
        }
    }

    /**
     * A request.
     *
     * @param   date        java.lang.String
     * @param   time        java.lang.String
     * @param   ipAddress   java.lang.String
     */
    record Request(
            String date,
            String time,
            String ipAddress
    ){}

    /**
     * Demonstrate the FMT template processor.
     */
    class FMTTemplateProcessor implements Demo {
        @Override
        public void demo() {
            logger.entry();

            Rectangle[] zone = new Rectangle[] {
                    new Rectangle("Addison", 17.8, 31.4),
                    new Rectangle("Baby", 9.6, 12.4),
                    new Rectangle("Carrie", 7.1, 11.23),
            };

            String table = FMT."""

                Description     Width    Height     Area
                %-12s\{zone[0].name}  %7.2f\{zone[0].width}  %7.2f\{zone[0].height}     %7.2f\{zone[0].area()}
                %-12s\{zone[1].name}  %7.2f\{zone[1].width}  %7.2f\{zone[1].height}     %7.2f\{zone[1].area()}
                %-12s\{zone[2].name}  %7.2f\{zone[2].width}  %7.2f\{zone[2].height}     %7.2f\{zone[2].area()}
                \{" ".repeat(28)} Total %7.2f\{zone[0].area() + zone[1].area() + zone[2].area()}
                """;

            logger.info(table);

            logger.exit();
        }
    }

    /**
     * A rectangle.
     *
     * @param   name    java.lang.String
     * @param   width   double
     * @param   height  double
     */
    record Rectangle(String name, double width, double height) {
        /**
         * Return the area of the rectangle.
         *
         * @return  double
         */
        double area() {
            return this.width * this.height;
        }
    }
}
