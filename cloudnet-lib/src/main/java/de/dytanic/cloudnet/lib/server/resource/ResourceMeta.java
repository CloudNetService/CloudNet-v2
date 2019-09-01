/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.resource;

/**
 * Created by Tareko on 02.09.2017.
 */
public class ResourceMeta {

    private double cpuUsage; //%

    private long heapMemory; //KB

    private long maxHeapMemory; //KB

    public ResourceMeta(double cpuUsage, long heapMemory, long maxHeapMemory) {
        this.cpuUsage = cpuUsage;
        this.heapMemory = heapMemory;
        this.maxHeapMemory = maxHeapMemory;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public long getHeapMemory() {
        return heapMemory;
    }

    public long getMaxHeapMemory() {
        return maxHeapMemory;
    }
}