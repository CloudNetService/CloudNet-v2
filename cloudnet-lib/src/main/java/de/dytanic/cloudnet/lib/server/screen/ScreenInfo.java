/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;

public class ScreenInfo {

    private ServiceId serviceId;

    private String line;

    public ScreenInfo(ServiceId serviceId, String line) {
        this.serviceId = serviceId;
        this.line = line;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public String getLine() {
        return line;
    }
}