/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components;

/**
 * Created by Tareko on 24.07.2017.
 */
public class WrapperMeta {

    private String id;

    private String hostName;

    private String user;

    public WrapperMeta(String id, String hostName, String user) {
        this.id = id;
        this.hostName = hostName;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUser() {
        return user;
    }
}