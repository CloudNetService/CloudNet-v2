/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.screen;


import de.dytanic.cloudnet.lib.NetworkUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ScreenProvider {

    private final java.util.Map<Screenable, ScreenLoader> loads = NetworkUtils.newConcurrentHashMap();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void putScreenRequest(Screenable screenable)
    {
        ScreenLoader screenLoader = new ScreenLoader(screenable);
        executorService.execute(screenLoader);
        loads.put(screenable, screenLoader);
    }

    public void cancel(Screenable screenable)
    {
        if(loads.containsKey(screenable))
        {
            loads.get(screenable).cancel();
            loads.remove(screenable);
        }
    }

    public boolean contains(Screenable screenable)
    {
        return loads.containsKey(screenable);
    }

    public void shutdown()
    {
        for(Screenable screenable : loads.keySet())
        {
            cancel(screenable);
        }

        loads.clear();
        executorService.shutdownNow();
    }
}