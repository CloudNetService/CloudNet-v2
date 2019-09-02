package de.dytanic.cloudnet.lib.utility.threading;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 24.05.2017.
 */
public final class Scheduler implements TaskCancelable, Runnable {

    private final int ticks;
    private final Random random = new Random();
    private ConcurrentHashMap<Long, ScheduledTask> tasks = NetworkUtils.newConcurrentHashMap();

    public Scheduler(int ticks) {
        this.ticks = ticks;
    }

    public Scheduler() {
        this.ticks = 10;
    }

    public int getTicks() {
        return ticks;
    }

    public Random getRandom() {
        return random;
    }

    public ScheduledTask runTaskSync(Runnable runnable) {
        return runTaskDelaySync(runnable, 0);
    }

    public ScheduledTask runTaskDelaySync(Runnable runnable, int delayTicks) {
        return runTaskRepeatSync(runnable, delayTicks, -1);
    }

    public ScheduledTask runTaskRepeatSync(Runnable runnable, int delayTicks, int repeatDelay) {
        long id = random.nextLong();
        ScheduledTask task = new ScheduledTask(id, runnable, delayTicks, repeatDelay);
        this.tasks.put(id, task);
        return task;
    }

    public ScheduledTask runTaskAsync(Runnable runnable) {
        return runTaskDelayAsync(runnable, 0);
    }

    public ScheduledTask runTaskDelayAsync(Runnable runnable, int delay) {
        return runTaskRepeatAsync(runnable, delay, -1);
    }

    public ScheduledTask runTaskRepeatAsync(Runnable runnable, int delay, int repeat) {
        long id = random.nextLong();
        ScheduledTask task = new ScheduledTaskAsync(id, runnable, delay, repeat, this);
        this.tasks.put(id, task);
        return task;
    }

    @Override
    public void cancelTask(Long id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel();
        }
    }

    @Override
    public void cancelAllTasks() {
        tasks.clear();
    }

    @Override
    @Deprecated //This Method use the Thread for the Task Handling
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000 / ticks);
            } catch (InterruptedException e) {
            }

            if (tasks.isEmpty()) {
                continue;
            }

            ConcurrentHashMap<Long, ScheduledTask> tasks = this.tasks; //For a Performance optimizing

            for (ScheduledTask task : tasks.values()) {

                if (task.isInterrupted()) {
                    this.tasks.remove(task.getTaskId());
                    continue;
                }

                task.run();
            }
        }
    }
}
