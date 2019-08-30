/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

/**
 * Created by Tareko on 22.08.2017.
 */
public class CustomServerConfig {

    private String serverId;

    private int memory;

    private String group, wrapper;

    private boolean onlineMode;

    public CustomServerConfig(String serverId, int memory, String group, String wrapper, boolean onlineMode) {
        this.serverId = serverId;
        this.memory = memory;
        this.group = group;
        this.wrapper = wrapper;
        this.onlineMode = onlineMode;
    }

    public int getMemory() {
        return memory;
    }

    public String getServerId() {
        return serverId;
    }

    public String getWrapper() {
        return wrapper;
    }

    public String getGroup() {
        return group;
    }
}