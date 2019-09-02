package de.dytanic.cloudnet.lib.scheduler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Tareko on 19.01.2018.
 */
public class TaskEntryFuture<T> implements Future<T> {

    protected volatile boolean waits;
    private TaskEntry<T> entry;

    public TaskEntryFuture(TaskEntry<T> entry, boolean waits) {
        this.entry = entry;
        this.waits = waits;
    }

    public TaskEntry<T> getEntry() {
        return entry;
    }

    public boolean isWaits() {
        return waits;
    }

    @Override
    public boolean cancel(boolean pMayInterruptIfRunning) {

        if (pMayInterruptIfRunning) {
            entry.task = null;
            entry.repeat = 0;
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return entry.task == null;
    }


    @Override
    public boolean isDone() {
        return entry.completed;
    }


    @Override
    public synchronized T get() throws InterruptedException, ExecutionException {
        waits = true;
        while (!isDone()) {
            this.wait();
        }

        return entry.value;
    }


    @Override
    public synchronized T get(long pTimeout, TimeUnit pUnit) throws InterruptedException, ExecutionException, TimeoutException {

        waits = true;
        /*
        long timeout = System.currentTimeMillis() + (pUnit.toMillis(pTimeout));

        while (!isDone()) {
            if (timeout < System.currentTimeMillis()) Thread.sleep(0, 200000);
            else throw new TimeoutException();
        }*/

        while (!isDone()) {
            this.wait(pUnit.toMillis(pTimeout));
        }

        return entry.value;
    }

}
