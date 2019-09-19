/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Asynchronous print stream that takes print statements without blocking.
 */
public class AsyncPrintStream extends PrintStream {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread worker = new WorkerThread(queue);

    /**
     * Constructs a new asynchronous print stream.
     *
     * @param out the output stream to write to
     *
     * @throws UnsupportedEncodingException when UTF-8 is mysteriously unavailable
     */
    public AsyncPrintStream(OutputStream out) throws UnsupportedEncodingException {
        super(out, true, StandardCharsets.UTF_8.name());
    }

    public Thread getWorker() {
        return worker;
    }

    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    @Override
    public void print(boolean x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(char x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(int x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(int x) {
        super.print(x);
    }

    @Override
    public void print(long x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(long x) {
        super.print(x);
    }

    @Override
    public void print(float x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(double x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(double x) {
        super.print(x);
    }

    @Override
    public void print(char[] x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void print(String x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    private void print0(String x) {
        super.print(x);
    }

    @Override
    public void print(Object x) {
        if (Thread.currentThread() != worker) {
            queue.offer(() -> print0(x));
        } else {
            super.print(x);
        }
    }

    @Override
    public void println() {
        queue.offer(this::println0);
    }

    private void println0() {
        super.println();
    }

    @Override
    public void println(boolean x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(char x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(int x) {
        queue.offer(() -> println0(x));
    }

    private void println0(int x) {
        super.println(x);
    }

    @Override
    public void println(long x) {
        queue.offer(() -> println0(x));
    }

    private void println0(long x) {
        super.println(x);
    }

    @Override
    public void println(float x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(double x) {
        queue.offer(() -> println0(x));
    }

    private void println0(double x) {
        super.println(x);
    }

    @Override
    public void println(char[] x) {
        queue.offer(() -> println0(x));
    }

    @Override
    public void println(String x) {
        queue.offer(() -> println0(x));
    }

    private void println0(String x) {
        super.println(x);
    }

    @Override
    public void println(Object x) {
        queue.offer(() -> println0(x));
    }

    private void println0(Object x) {
        super.println(x);
    }

    private void println0(char[] x) {
        super.println(x);
    }

    private void println0(float x) {
        super.println(x);
    }

    private void println0(char x) {
        super.println(x);
    }

    private void println0(boolean x) {
        super.println(x);
    }

    private void print0(Object x) {
        super.print(x);
    }

    private void print0(char[] x) {
        super.print(x);
    }

    private void print0(float x) {
        super.print(x);
    }

    private void print0(char x) {
        super.print(x);
    }

    private void print0(boolean x) {
        super.print(x);
    }

    /**
     * A worker thread for the {@link AsyncPrintStream}.
     */
    private static class WorkerThread extends Thread {

        private final BlockingQueue<Runnable> queue;

        /**
         * Constructs an worker thread that takes work from {@code queue}.
         * Automatically started until interrupted.
         *
         * @param queue the blocking queue to take work from
         */
        WorkerThread(BlockingQueue<Runnable> queue) {
            this.queue = queue;
            setPriority(Thread.MIN_PRIORITY);
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
