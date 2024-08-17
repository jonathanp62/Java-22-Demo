package net.jmp.demo.java22.scopes;

/*
 * (#)CustomScope.java  0.6.0   08/17/2024
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

import java.util.Queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;

import java.util.stream.Stream;

public final class CustomScope<T> extends StructuredTaskScope<T> {
    /** A queue of completed results. */
    private final Queue<T> results = new ConcurrentLinkedQueue<>();

    /** A queue of failed throwables. */
    private final Queue<Throwable> throwables = new ConcurrentLinkedQueue<>();

    /**
     * The default constructor.
     */
    public CustomScope() {
        super("Custom scope", Thread.ofVirtual().factory());
    }

    /**
     * Invoked by a subtask when it completes successfully or
     * fails in this task scope. This method is not invoked if
     * a subtask completes after the task scope is shut down.
     *
     * @param   subtask java.util.concurrent.StructuredTaskScope.Subtask
     */
    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        super.handleComplete(subtask);

        if (subtask.state() == Subtask.State.SUCCESS) {
            this.results.add(subtask.get());
        }

        if (subtask.state() == Subtask.State.FAILED) {
            this.throwables.add(subtask.exception());
        }
    }

    /**
     * Wait for all subtasks started in this task scope to finish
     * or the task scope to shut down. This method waits for all
     * subtasks by waiting for all threads started in this task
     * scope to finish execution. It stops waiting when all threads
     * finish, the task scope is shut down, or the current thread is
     * interrupted.
     *
     * @return  net.jmp.demo.java22.scopes.CustomScope
     * @throws  java.lang.InterruptedException
     */
    @Override
    public CustomScope<T> join() throws InterruptedException {
        super.join();

        return this;
    }

    /**
     * Returns a stream of completed results from
     * the subtasks that completed successfully.
     *
     * @return  java.util.stream.Stream&lt;T&gt;
     */
    public Stream<T> results() {
        super.ensureOwnerAndJoined();

        return this.results.stream();
    }

    /**
     * Returns a stream of failed throwables from
     * the subtasks that completed successfully.
     *
     * @return  java.util.stream.Stream&lt;java.lang.Throwable&gt;
     */
    public Stream<Throwable> throwables() {
        super.ensureOwnerAndJoined();

        return this.throwables.stream();
    }
}
