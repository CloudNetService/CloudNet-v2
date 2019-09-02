/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service.wrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperScreen {

    private String wrapperId;

    private String consoleLine;

    public WrapperScreen(String wrapperId, String consoleLine) {
        this.wrapperId = wrapperId;
        this.consoleLine = consoleLine;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public String getConsoleLine() {
        return consoleLine;
    }
}