/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 02.09.2017.
 */
@Getter
@AllArgsConstructor
public class ResourceMeta {

    private double cpuUsage; //%

    private long heapMemory; //KB

    private long maxHeapMemory; //KB

}