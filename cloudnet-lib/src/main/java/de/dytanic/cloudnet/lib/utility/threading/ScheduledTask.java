package de.dytanic.cloudnet.lib.utility.threading;

import lombok.Getter;

/**
 * Created by Tareko on 24.05.2017.
 */
@Getter
public class ScheduledTask
            implements Runnable{

    protected long taskId;
    protected Runnable runnable;
    protected int delay;
    protected int repeatDelay;
    protected boolean interrupted;

    protected int delayTime;
    protected int repeatTime;

    public ScheduledTask(long taskId, Runnable runnable, int delay, int repeatDelay)
    {
        this.taskId = taskId;
        this.runnable = runnable;
        this.delay = delay != -1 && delay != 0 ? delay : 0;
        this.repeatDelay = repeatDelay != -1 ? repeatDelay : 0;
        this.interrupted = false;

        this.delayTime = this.delay;
        this.repeatTime = repeatDelay == 0 ? -1 : repeatDelay;
    }

    protected boolean isAsync()
    {
        return false;
    }

    @Override
    public void run()
    {
        if(interrupted) return;

        if(delay != 0
                && delayTime != 0)
        {
            delayTime--;
            return;
        }

        if(repeatTime > 0)
        {
            repeatTime--;
        }
        else
        {
            runnable.run();
            if(repeatTime == -1)
            {
                cancel();
                return;
            }
            repeatTime = repeatDelay;
        }

    }

    public void cancel()
    {
        this.interrupted = true;
    }
}
