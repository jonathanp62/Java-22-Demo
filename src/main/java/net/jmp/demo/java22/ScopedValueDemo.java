package net.jmp.demo.java22;

/*
 * (#)ScopedValueDemo.java  0.2.0   08/04/2024
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

import java.util.UUID;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;

import org.slf4j.LoggerFactory;

import org.slf4j.ext.XLogger;

/**
 * The class that demonstrates the scoped value.
 *
 * A scoped value is a container object that allows
 * a data value to be safely and efficiently shared
 * by a method with its direct and indirect callees
 * within the same thread, and with child threads,
 * without resorting to method parameters. It is a
 * variable of type ScopedValue. Typically, it is
 * declared as a final static field, and its accessibility
 * is set to private so that it cannot be directly accessed
 * by code in other classes.
 */
final class ScopedValueDemo implements Demo {
    /** The logger. */
    private final XLogger logger = new XLogger(LoggerFactory.getLogger(this.getClass().getName()));

    /** A scoped value UUID. */
    private static final ScopedValue<String> UID = ScopedValue.newInstance();

    /** A scoped value name. */
    private static final ScopedValue<String> NAME = ScopedValue.newInstance();

    /**
     * The default constructor.
     */
    ScopedValueDemo() {
        super();
    }

    /**
     * The demo method.
     */
    @Override
    public void demo() {
        this.logger.entry();

        this.basic();
        this.multiples();
        this.rebinding();
        this.inheritance();

        this.logger.exit();
    }

    /**
     * Basic usage. Bind 'uuid' to UUID
     * and pass it to the runnable.
     */
    private void basic() {
        this.logger.entry();

        final String uuid = UUID.randomUUID().toString();

        // The 'where' method returns a ScopedValue.Carrier object

        ScopedValue
                .where(UID, uuid)   // Bind 'uuid' to the scoped value
                .run(() -> {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("UUID: {}", UID.get());

                        if (UID.isBound()) {
                            this.logger.info("UID is bound");
                        }
                    }
                });

        this.logger.exit();
    }

    /**
     * Multiple bindings.
     */
    private void multiples() {
        this.logger.entry();

        final String uuid = UUID.randomUUID().toString();
        final String name = "Parker";

        ScopedValue
                .where(UID, uuid)
                .where(NAME, name)
                .run(() -> {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("UUID: {}", UID.get());
                        this.logger.info("NAME: {}", NAME.get());
                    }
                });

        this.logger.exit();
    }

    /**
     * Rebinding a scoped value.
     */
    private void rebinding() {
        this.logger.entry();

        ScopedValue.where(NAME, "Jonathan").run(this::bar);

        this.logger.exit();
    }

    /**
     * Method bar.
     */
    private void bar() {
        this.logger.entry();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("NAME: {}", NAME.get());   // Jonathan
        }

        ScopedValue.where(NAME, "Martin").run(this::baz);

        if (this.logger.isInfoEnabled()) {
            this.logger.info("NAME: {}", NAME.get());   // Jonathan
        }

        this.logger.exit();
    }

    /**
     * Method baz.
     */
    private void baz() {
        this.logger.entry();

        if (this.logger.isInfoEnabled()) {
            this.logger.info("NAME: {}", NAME.get());   // Martin
        }

        this.logger.exit();
    }

    /**
     * Inheritance or sharing across threads.
     */
    private void inheritance() {
        this.logger.entry();

        final Callable<String> childTask1 = () -> {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("NAME 1: {}", NAME.get());   // Martin
            }

            return NAME.get() + ":1";
        };

        final Callable<String> childTask2 = () -> {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("NAME 2: {}", NAME.get());   // Martin
            }

            return NAME.get() + ":2";
        };

        final Callable<String> childTask3 = () -> {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("NAME 3: {}", NAME.get());   // Martin
            }

            return NAME.get() + ":3";
        };

        ScopedValue.runWhere(NAME, "Duke", () -> {
            try (final var scope = new StructuredTaskScope<String>()) {
                final StructuredTaskScope.Subtask<String> subtask1 = scope.fork(childTask1);
                final StructuredTaskScope.Subtask<String> subtask2 = scope.fork(childTask2);
                final StructuredTaskScope.Subtask<String> subtask3 = scope.fork(childTask3);

                scope.join();

                this.logSubtaskStatus(subtask1, 1);
                this.logSubtaskStatus(subtask2, 2);
                this.logSubtaskStatus(subtask3, 3);
             } catch (final InterruptedException ie) {
                this.logger.error(ie.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        this.logger.exit();
    }

    /**
     * Log the status of a subtask.
     *
     * @param   subtask java.util.concurrent.StructuredTaskScope.Subtask&lt;java.lang.String&gt;
     * @param   item    int
     */
    private void logSubtaskStatus(final StructuredTaskScope.Subtask<String> subtask, final int item) {
        this.logger.entry(subtask, item);

        assert subtask != null;

        if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
            this.logger.info("Child task {}: {}", item, subtask.get());
        } else if (subtask.state() == StructuredTaskScope.Subtask.State.FAILED) {
            this.logger.error("Child task {} failed: {}", item, subtask.exception().getMessage());
        } else {
            this.logger.error("Child task {} result or exception is not available", item);
        }

        this.logger.exit();
    }
}
