package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

/**
 * Created by Tareko on 11.11.2017.
 */
public class CPUUsageHandler implements IWrapperHandler {

    private int tick = 0;

    @Override
    public void run(CloudNetWrapper obj)
    {
        CloudNetWrapper.getInstance().getCpuUsageEntries().add(NetworkUtils.cpuUsage());
        tick++;

        if(tick == 40) {
            tick = 0;
            CloudNetWrapper.getInstance().getCpuUsageEntries().clear();
        }
    }

    @Override
    public int getTicks()
    {
        return 1;
    }
}