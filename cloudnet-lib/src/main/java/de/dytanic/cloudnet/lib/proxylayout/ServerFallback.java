/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

/**
 * Created by Tareko on 05.10.2017.
 */
public class ServerFallback {

    private String group;

    private String permission;

    public ServerFallback(String group, String permission) {
        this.group = group;
        this.permission = permission;
    }

    public String getGroup() {
        return group;
    }

    public String getPermission() {
        return permission;
    }
}