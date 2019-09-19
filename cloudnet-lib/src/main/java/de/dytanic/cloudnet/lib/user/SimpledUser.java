/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.user;

/**
 * Created by Tareko on 24.09.2017.
 */
public class SimpledUser {

    private String userName;

    private String apiToken;

    public SimpledUser(String userName, String apiToken) {
        this.userName = userName;
        this.apiToken = apiToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getUserName() {
        return userName;
    }
}