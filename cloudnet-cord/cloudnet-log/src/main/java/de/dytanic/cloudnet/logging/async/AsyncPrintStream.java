/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging.async;

import lombok.Getter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Tareko on 23.09.2017.
 */
@Getter
public class AsyncPrintStream extends PrintStream {

    private static final BlockingQueue<Runnable> asyncQueue = new LinkedBlockingQueue<>();
    private static final Thread worker = new Thread() {

        {
            setPriority(Thread.MIN_PRIORITY);
            setDaemon(true);
            start();
        }

        @Override
        public void run()
        {
            while (!isInterrupted())
            {
                try
                {
                    Runnable runnable = asyncQueue.take();
                    runnable.run();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    public AsyncPrintStream(OutputStream out) throws UnsupportedEncodingException
    {
        super(out, true, StandardCharsets.UTF_8.name());
    }

    private void println0()
    {
        super.println();
    }

    @Override
    public void println()
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0();
            }
        });

    }

    private void println0(int x)
    {
        super.println(x);
    }

    @Override
    public void println(int x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(String x)
    {
        super.println(x);
    }

    @Override
    public void println(String x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });
    }

    private void println0(long x)
    {
        super.println(x);
    }

    @Override
    public void println(long x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(char x)
    {
        super.println(x);
    }

    @Override
    public void println(char x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(double x)
    {
        super.println(x);
    }

    @Override
    public void println(double x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(float x)
    {
        super.println(x);
    }

    @Override
    public void println(float x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(Object x)
    {
        super.println(x);
    }

    @Override
    public void println(Object x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(char[] x)
    {
        super.println(x);
    }

    @Override
    public void println(char[] x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

    private void println0(boolean x)
    {
        super.println(x);
    }

    @Override
    public void println(boolean x)
    {
        asyncQueue.offer(new Runnable() {
            @Override
            public void run()
            {
                println0(x);
            }
        });

    }

	/* ============================================== */

    private void print0(int x)
    {
        super.print(x);
    }

    @Override
    public void print(int x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(String x)
    {
        super.print(x);
    }

    @Override
    public void print(String x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(long x)
    {
        super.print(x);
    }

    @Override
    public void print(long x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(char x)
    {
        super.print(x);
    }

    @Override
    public void print(char x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(double x)
    {
        super.print(x);
    }

    @Override
    public void print(double x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(float x)
    {
        super.print(x);
    }

    @Override
    public void print(float x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(Object x)
    {
        super.print(x);
    }

    @Override
    public void print(Object x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(char[] x)
    {
        super.print(x);
    }

    @Override
    public void print(char[] x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

    private void print0(boolean x)
    {
        super.print(x);
    }

    @Override
    public void print(boolean x)
    {
        if (Thread.currentThread() != worker)
        {
            asyncQueue.offer(new Runnable() {
                @Override
                public void run()
                {
                    print0(x);
                }
            });

        } else
        {
            super.print(x);
        }
    }

}