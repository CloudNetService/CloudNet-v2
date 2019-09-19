/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 13.09.2017.
 */
public class SimpledWrapperInfo implements Nameable {

    private String name;

    private String hostName;

    public SimpledWrapperInfo(String name, String hostName) {
        this.name = name;
        this.hostName = hostName;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }
}