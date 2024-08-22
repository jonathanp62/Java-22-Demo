package net.jmp.demo.java22;

/*
 * (#)Main.java 0.7.1   08/22/2024
 * (#)Main.java 0.6.0   08/15/2024
 * (#)Main.java 0.5.0   08/10/2024
 * (#)Main.java 0.3.0   08/07/2024
 * (#)Main.java 0.2.0   08/04/2024
 * (#)Main.java 0.1.0   08/02/2024
 *
 * @author   Jonathan Parker
 * @version  0.7.1
 * @since    0.1.0
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

import java.util.List;
import java.util.Objects;

import net.jmp.demo.java22.demos.*;

import static net.jmp.demo.java22.util.LoggerUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class.
 */
final class Main implements Runnable {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** The command line arguments. */
    private final String[] arguments;

    /**
     * A constructor that takes the
     * command line arguments from
     * the bootstrap class.
     */
    Main(final String[] args) {
        super();

        this.arguments = Objects.requireNonNull(args);
    }

    /**
     * The run method.
     */
    @Override
    public void run() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        if (this.logger.isInfoEnabled() || this.logger.isWarnEnabled() || this.logger.isErrorEnabled()) {
            final String name = Name.NAME_STRING;
            final String version = Version.VERSION_STRING;
            final String greeting = STR."\{name} \{version}";

            System.out.println(greeting);
        } else {
            this.logger.debug("{} {}", Name.NAME_STRING, Version.VERSION_STRING);
        }

        this.runDemos();

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /**
     * Method that runs the demo classes.
     */
    private void runDemos() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        List<Demo> demos = List.of(
                new KeyedFunctionExecutorDemo(),
                new AppliedCollectionDemo(),
                new ScopedValueDemo(),
                new StreamGatherersDemo(),
                new StringTemplatesDemo(),
                new BeforeSuperDemo(),
                new UnnamedVariablesDemo(),
                new StructuredConcurrencyDemo()
        );

        demos.forEach(Demo::demo);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
