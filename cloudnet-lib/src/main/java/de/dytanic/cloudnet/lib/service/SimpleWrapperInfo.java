/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

/**
 * Created by Tareko on 13.09.2017.
 */
public class SimpleWrapperInfo implements Nameable {

    private String name;

    private String hostName;

    public SimpleWrapperInfo(String name, String hostName) {
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
