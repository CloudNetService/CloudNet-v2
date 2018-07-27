package de.dytanic.cloudnet.lib;

import de.dytanic.cloudnet.lib.utility.threading.ScheduledTask;
import de.dytanic.cloudnet.lib.utility.threading.Scheduler;


public class Cache<K, V> {

    private ScheduledTask task;
    private java.util.Map<K, V> values;

    public Cache(Scheduler scheduler)
    {
        values = NetworkUtils.newConcurrentHashMap();
        task = scheduler.runTaskRepeatSync(new Runnable() {
            @Override
            public void run()
            {
                Cache.this.values.clear();
            }
        }, 0, scheduler.getTicks() * 100);
    }

    public Cache<K, V> append(K key, V value)
    {
        values.put(key, value);
        return this;
    }

    public V get(K key)
    {
        return values.get(key);
    }

    public Cache<K, V> clear()
    {
        values.clear();
        return this;
    }

    public boolean shutdown()
    {
        task.cancel();
        return true;
    }

}