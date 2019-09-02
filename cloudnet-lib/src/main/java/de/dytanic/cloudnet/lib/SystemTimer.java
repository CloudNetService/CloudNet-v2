/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib;

import java.lang.management.ManagementFactory;

/**
 * Created by Tareko on 23.09.2017.
 */
public class SystemTimer extends Thread {

    public SystemTimer() {
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            System.out.println("Memory [\"" + (ManagementFactory.getMemoryMXBean()
                                                                .getHeapMemoryUsage()
                                                                .getUsed() / 1024) + "KB\"] | CPU Programm [\"" + NetworkUtils.DECIMAL_FORMAT
                .format(NetworkUtils.internalCpuUsage()) + "\"] | CPU System [\"" + NetworkUtils.DECIMAL_FORMAT.format(NetworkUtils.cpuUsage()) + "\"]");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }
}
